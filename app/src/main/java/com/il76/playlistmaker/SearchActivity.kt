package com.il76.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.Log
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

    /**
     * Статусы результатов поиска
     */
    enum class ErrorStatus {
        NONE, ERROR_NET, EMPTY_RESULT
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
                val trackApiService = retrofit.create<TrackAPIService>()
                trackApiService.getTracks(inputEditText.text.toString()).enqueue(object : Callback<TracksList> {
                    /**
                     * Отображение или скрытие информации об отсутствии поисковой выдачи
                     */
                    private fun displayError(status: ErrorStatus) {
                        val searchError = findViewById<LinearLayout>(R.id.search_error)
                        val searchRefresh = findViewById<Button>(R.id.search_error_refresh)
                        val searchImage = findViewById<ImageView>(R.id.search_error_image)
                        val searchErrorText = findViewById<TextView>(R.id.search_error_text)
                        when (status) {
                            ErrorStatus.NONE -> searchError.isVisible = false
                            ErrorStatus.ERROR_NET -> {
                                searchError.isVisible = true
                                searchRefresh.isVisible = true
                                searchImage.setImageResource(R.drawable.search_network_error)
                                searchErrorText.text = getText(R.string.search_network_error)
                            }
                            ErrorStatus.EMPTY_RESULT -> {
                                searchError.isVisible = true
                                searchRefresh.isVisible = false
                                searchImage.setImageResource(R.drawable.search_nothing_found)
                                searchErrorText.text = getText(R.string.search_nothing_found)
                            }
                        }
                    }

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
                // true
            }
            false
        }
        val clearButton = findViewById<ImageView>(R.id.search_icon_clear)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val view = this.currentFocus
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
            val recyclerView = findViewById<RecyclerView>(R.id.track_list)
            recyclerView.removeAllViewsInLayout()
            recyclerView.isVisible = false
        }

        inputEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                clearButton.isVisible = !s.isNullOrEmpty()
                searchValue = s.toString()
            },
        )

        fillMockTracks()

        recyclerView = findViewById<RecyclerView>(R.id.track_list)
        trackAdapter = TrackAdapter(trackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter


    /**
     * Наполняем список треков тестовыми значениями
     */
    private fun fillMockTracks() {
        trackList.add(Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"))
        trackList.add(Track(
            "Billie Jean",
            "Michael Jackson",
             "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"))
        trackList.add(Track(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"))
        trackList.add(Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"))
        trackList.add(Track(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"))
        trackList.add(Track(
            "Invalid track with a very very very very very very very very very very long name",
            "Invalid artist with a  very very very very long name",
            "0:00",
            ""))
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_QUERY, "")
        val inputEditText = findViewById<EditText>(R.id.search_edit_text)
        inputEditText.setText(searchValue)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchValue)
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
    }
}