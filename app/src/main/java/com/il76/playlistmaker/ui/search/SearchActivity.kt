package com.il76.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.il76.playlistmaker.Creator
import com.il76.playlistmaker.application.App
import com.il76.playlistmaker.R
import com.il76.playlistmaker.data.dto.TracksSearchResponse
import com.il76.playlistmaker.data.network.TrackAPIService
import com.il76.playlistmaker.domain.api.TrackSearchHistory
import com.il76.playlistmaker.domain.api.TracksList
import com.il76.playlistmaker.databinding.ActivitySearchBinding
import com.il76.playlistmaker.domain.api.TracksInteractor
import com.il76.playlistmaker.domain.api.TracksRepository
import com.il76.playlistmaker.domain.models.Track
import com.il76.playlistmaker.ui.player.PlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private var searchValue: String = ""

    private val trackList = arrayListOf<Track>()

    private val trackInteractorImpl = Creator.provideTracksInteractor()

    private val trackHistoryInteractorImpl = Creator.provideTracksHistoryInteractor()

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchRequest() }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private var _binding: ActivitySearchBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchRequest() {
        // Включаем историю при пустом тексте и отключаем при непустом
        toggleSearchHistory(searchValue.isEmpty())
        binding.searchIconClear.isVisible = searchValue.isNotEmpty()
        if (searchValue.isEmpty()) {
            displayError(ErrorStatus.NONE)
        } else {
            doSearch()
        }
    }

    /**
     * Отображение или скрытие информации об отсутствии поисковой выдачи
     */
    private fun displayError(status: ErrorStatus) {
        when (status) {
            ErrorStatus.NONE -> {
                binding.searchError.isVisible = false
                recyclerView.isVisible = true
            }
            ErrorStatus.ERROR_NET -> {
                binding.searchError.isVisible = true
                binding.searchErrorRefresh.isVisible = true
                binding.searchErrorImage.setImageResource(R.drawable.search_network_error)
                binding.searchErrorText.text = getText(R.string.search_network_error)
                recyclerView.isVisible = false
            }
            ErrorStatus.EMPTY_RESULT -> {
                binding.searchError.isVisible = true
                binding.searchErrorRefresh.isVisible = false
                binding.searchErrorImage.setImageResource(R.drawable.search_nothing_found)
                binding.searchErrorText.text = getText(R.string.search_nothing_found)
                recyclerView.isVisible = false
            }
        }
    }

    private fun doSearch() {
        if (searchValue.isEmpty()) {
            return
        }
        binding.progressBar.isVisible = true
        trackInteractorImpl.searchTracks(searchValue,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    handler.post {
                        if (foundTracks == null) {
                            displayError(ErrorStatus.ERROR_NET)
                        } else if (foundTracks.isNotEmpty()) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            displayError(ErrorStatus.NONE)
                            trackAdapter.notifyDataSetChanged()
                        } else {
                            displayError(ErrorStatus.EMPTY_RESULT)
                        }
                    }
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.activitySearch) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // назад
        binding.activitySearchToolbar.setNavigationOnClickListener {
            this.finish()
        }

        // поисковая форма
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                doSearch()
                true
            }
            false
        }

        binding.searchIconClear.setOnClickListener {
            binding.searchEditText.setText("")
                val inputMethodManager = getSystemService<InputMethodManager>()
            val view = this.currentFocus
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
            displayError(ErrorStatus.NONE)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            toggleSearchHistory(true)
        }
        // обработчик фокуса на текстовое поле. Включаем кнопки только если есть фокус и пустой текст
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            toggleSearchHistory(hasFocus && binding.searchEditText.text.isNullOrEmpty())
        }
        binding.searchEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                searchValue = s.toString()
                searchDebounce()
            },
        )

        recyclerView = binding.trackList
        trackAdapter = TrackAdapter(trackList)
        trackAdapter.onClickListener(
            object : TrackAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, view: View) {
                    if (clickDebounce()) {
                        if (trackList[position].trackId > 0) {
                            val elem = trackList[position]
                            trackHistoryInteractorImpl.addTrack(elem)

//                          Если перестраивать - долгое ожидание запуска следующей активити.
//                          Если не перестраивать - при возврате текущий элемент не прыгает наверх
                            if (binding.searchHistoryClear.isVisible) { //если кнопка очистки отображается - значит сейчас режим истории и нужно её перестраивать
                                trackList.clear()
                                trackList.addAll(trackHistoryInteractorImpl.getTracks().reversed())
                                trackAdapter.notifyDataSetChanged()
                            }
                            val json = Creator.provideGson().toJson(elem)
                            val intent = Intent(applicationContext, PlayerActivity::class.java)
                            intent.putExtra("track", json)
                            startActivity(intent)

                        } else {
                            Toast.makeText(
                                applicationContext,
                                applicationContext.getString(R.string.no_track_id),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        binding.searchErrorRefresh.setOnClickListener {
            doSearch()
        }
        binding.searchHistoryClear.setOnClickListener {
            trackHistoryInteractorImpl.clearHistory()
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
        if (trackHistoryInteractorImpl.getTracks().isEmpty()) {
            isVisible = false // нет истории - нет истории
        }
        binding.searchHistoryTitle.isVisible = isVisible
        binding.searchHistoryClear.isVisible = isVisible

        if (isVisible && trackHistoryInteractorImpl.getTracks().isNotEmpty()) {
            trackList.clear()
            trackList.addAll(trackHistoryInteractorImpl.getTracks().reversed())
            trackAdapter.notifyDataSetChanged()
        } else if (!isVisible && trackList.size > 0) {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_QUERY, "")
        binding.searchEditText.setText(searchValue)
        doSearch()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchValue)
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    /**
     * Статусы результатов поиска
     */
    private enum class ErrorStatus {
        NONE, ERROR_NET, EMPTY_RESULT
    }
}