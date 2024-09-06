package com.il76.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private val trackList = arrayListOf<Track>()

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
        val clearButton = findViewById<ImageView>(R.id.search_icon_clear)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val view = this.currentFocus
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        val searchTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(searchTextWatcher)

        fillMockTracks()

        val recyclerView = findViewById<RecyclerView>(R.id.track_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter

    }

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


    private var searchValue: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchValue)
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
    }
}