package com.il76.playlistmaker.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentSearchBinding
import com.il76.playlistmaker.player.ui.PlayerFragment
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModel<SearchViewModel>()

    private var searchValue: String = ""

    private val trackList = arrayListOf<Track>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var textWatcher: TextWatcher

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState !== null) {
            searchValue = savedInstanceState.getString(SEARCH_QUERY, "")
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.addToHistory(track)
            findNavController().navigate(
                R.id.action_search_fragment_to_playerFragment,
                PlayerFragment.createArgs(viewModel.provideTrackData(track))
            )
        }


        binding.searchEditText.setText(searchValue)
        viewModel.doSearch(searchValue)
        viewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }

        // поисковая форма
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.doSearch(searchValue)
                true
            }
            false
        }

        binding.searchIconClear.setOnClickListener {
            binding.searchEditText.setText("")
            val inputMethodManager = requireActivity().getSystemService<InputMethodManager>()
            inputMethodManager?.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
            viewModel.setSearchText("")
            viewModel.toggleHistory(true)
        }

        // обработчик фокуса на текстовое поле. Включаем кнопки только если есть фокус и пустой текст
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.toggleHistory(hasFocus && binding.searchEditText.text.isNullOrEmpty())
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchValue = s?.toString() ?: ""
                viewModel.searchDebounce(
                    changedText = searchValue
                )
                binding.searchIconClear.isVisible = searchValue.isNotEmpty()
                if (searchValue.isEmpty()) {
                    viewModel.toggleHistory(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }
        recyclerView = binding.trackList

        trackAdapter = TrackAdapter(trackList, onTrackClickDebounce)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = trackAdapter

        binding.searchErrorRefresh.setOnClickListener {
            viewModel.doSearch(searchValue)
        }
        binding.searchHistoryClear.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun renderState(state: SearchState) {
        if (state.status == SearchState.ErrorStatus.LOADING) {
            binding.progressBar.isVisible = true
        } else {
            binding.progressBar.isVisible = false
        }
        binding.searchHistoryTitle.isVisible = false
        binding.searchHistoryClear.isVisible = false
        binding.searchError.isVisible = false
        binding.searchErrorRefresh.isVisible = false
        when (state.status) {
            SearchState.ErrorStatus.NONE -> {
            }
            SearchState.ErrorStatus.ERROR_NET -> {
                binding.searchError.isVisible = true
                binding.searchErrorRefresh.isVisible = true
                binding.searchErrorImage.setImageResource(R.drawable.search_network_error)
                binding.searchErrorText.text = getText(R.string.search_network_error)
                recyclerView.isVisible = false
            }
            SearchState.ErrorStatus.EMPTY_RESULT -> {
                binding.searchError.isVisible = true
                binding.searchErrorRefresh.isVisible = false
                binding.searchErrorImage.setImageResource(R.drawable.search_nothing_found)
                binding.searchErrorText.text = getText(R.string.search_nothing_found)
                recyclerView.isVisible = false
            }
            SearchState.ErrorStatus.LOADING -> {
                binding.searchHistoryTitle.isVisible = false
                binding.searchHistoryClear.isVisible = false
                binding.searchError.isVisible = false
                binding.searchErrorRefresh.isVisible = false
                recyclerView.isVisible = false
            }
            SearchState.ErrorStatus.SUCCESS -> {
                binding.searchError.isVisible = false
                recyclerView.isVisible = true
                trackList.clear()
                trackList.addAll(state.trackList)
                trackAdapter.notifyDataSetChanged()
            }
            SearchState.ErrorStatus.HISTORY -> {
                binding.searchHistoryTitle.isVisible = true
                binding.searchHistoryClear.isVisible = true
                trackList.clear()
                trackList.addAll(state.trackList.reversed())
                trackAdapter.notifyDataSetChanged()
                recyclerView.isVisible = true
            }
            SearchState.ErrorStatus.EMPTY_HISTORY -> {
                binding.searchHistoryTitle.isVisible = false
                binding.searchHistoryClear.isVisible = false
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireActivity(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchValue)
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}