package com.il76.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private var searchValue: String = ""

    private val trackList = arrayListOf<Track>()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // private lateinit var spListener: SharedPreferences.OnSharedPreferenceChangeListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyClear: Button

    /**
     * Статусы результатов поиска
     */
    private enum class ErrorStatus {
        NONE, ERROR_NET, EMPTY_RESULT
    }
    /**
     * Отображение или скрытие информации об отсутствии поисковой выдачи
     */
    private fun displayError(status: ErrorStatus) {
        val searchError = findViewById<LinearLayout>(R.id.search_error)
        val searchRefresh = findViewById<Button>(R.id.search_error_refresh)
        val searchImage = findViewById<ImageView>(R.id.search_error_image)
        val searchErrorText = findViewById<TextView>(R.id.search_error_text)
        when (status) {
            ErrorStatus.NONE -> {
                searchError.isVisible = false
                recyclerView.isVisible = true
            }
            ErrorStatus.ERROR_NET -> {
                searchError.isVisible = true
                searchRefresh.isVisible = true
                searchImage.setImageResource(R.drawable.search_network_error)
                searchErrorText.text = getText(R.string.search_network_error)
                recyclerView.isVisible = false
            }
            ErrorStatus.EMPTY_RESULT -> {
                searchError.isVisible = true
                searchRefresh.isVisible = false
                searchImage.setImageResource(R.drawable.search_nothing_found)
                searchErrorText.text = getText(R.string.search_nothing_found)
                recyclerView.isVisible = false
            }
        }
    }
    private fun doSearch() {
        if (searchValue.isEmpty()) {
            return
        }
        val trackApiService = retrofit.create<TrackAPIService>()
        trackApiService.getTracks(searchValue).enqueue(object : Callback<TracksList> {


            override fun onResponse(call: Call<TracksList>, response: Response<TracksList>) {

                // Получили ответ от сервера
                if (response.isSuccessful) {
                    // Наш запрос был удачным, получаем наши объекты
                    val body = response.body()
                    trackList.clear()
                    for (item in body?.results!!) {
                        trackList.add(item)
                    }
                    if (trackList.size == 0) {
                        displayError(ErrorStatus.EMPTY_RESULT)
                    } else {
                        displayError(ErrorStatus.NONE)
                    }
                    trackAdapter.notifyDataSetChanged()

                } else {
                    // Сервер отклонил наш запрос с ошибкой
                    displayError(ErrorStatus.ERROR_NET)
                }
            }

            override fun onFailure(call: Call<TracksList>, t: Throwable) {
                // Не смогли присоединиться к серверу
                // Выводим ошибку в лог, что-то пошло не так
                t.printStackTrace()
                displayError(ErrorStatus.ERROR_NET)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // назад
        val buttonBack = findViewById<MaterialToolbar>(R.id.activity_search_toolbar)
        buttonBack.setNavigationOnClickListener {
            this.finish()
        }

        // поисковая форма
        val inputEditText = findViewById<EditText>(R.id.search_edit_text)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSearch()
                true
            }
            false
        }

        val clearButton = findViewById<ImageView>(R.id.search_icon_clear)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val view = this.currentFocus
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
            displayError(ErrorStatus.NONE)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            toggleSearchHistory(true)
        }
        // обработчик фокуса на текстовое поле. Включаем кнопки только если есть фокус и пустой текст
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            toggleSearchHistory(hasFocus && inputEditText.text.isNullOrEmpty())
        }
        inputEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                // Включаем историю при пустом тексте и отключаем при непустом
                toggleSearchHistory(s.isNullOrEmpty())
                clearButton.isVisible = !s.isNullOrEmpty()
                searchValue = s.toString()
                if (s.isNullOrEmpty()) {
                    displayError(ErrorStatus.NONE)
                }
            },
        )

        recyclerView = findViewById<RecyclerView>(R.id.track_list)
        trackAdapter = TrackAdapter(trackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        val retrySearch = findViewById<Button>(R.id.search_error_refresh)
        retrySearch.setOnClickListener {
            doSearch()
        }
        historyClear = findViewById<Button>(R.id.search_history_clear)
        historyClear.setOnClickListener {
            App.instance.clearHistory()
            trackList.clear()
            toggleSearchHistory(false)
            trackAdapter.notifyDataSetChanged()
        }
        toggleSearchHistory(false)

    }

    /**
     * Переключатель видимости заголовка истории поиска и кнопки очистки
     */
    private fun toggleSearchHistory(visibility: Boolean) {
        var isVisible = visibility
        if (App.instance.trackListHistory.isEmpty()) {
            isVisible = false // нет истории - нет истории
        }
        val historyTitle = findViewById<TextView>(R.id.search_history_title)
        historyTitle.isVisible = isVisible

        historyClear.isVisible = isVisible

        if (isVisible && App.instance.trackListHistory.size > 0) {
            trackList.clear()
            trackList.addAll(App.instance.trackListHistory.reversed())
            trackAdapter.notifyDataSetChanged()
        } else if (!isVisible && trackList.size > 0) {
//            trackList.clear()
//            trackAdapter.notifyDataSetChanged()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_QUERY, "")
        val inputEditText = findViewById<EditText>(R.id.search_edit_text)
        inputEditText.setText(searchValue)
        doSearch()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchValue)
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
    }
}