package com.il76.playlistmaker.search.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.rememberAsyncImagePainter
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentSearchBinding
import com.il76.playlistmaker.player.ui.PlayerFragment
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.settings.ui.SettingsViewModel
import com.il76.playlistmaker.utils.InternetBroadcastReceiver
import com.il76.playlistmaker.utils.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

//class SearchFragment: Fragment() {
//
//    private lateinit var binding: FragmentSearchBinding
//    private val viewModel by viewModel<SearchViewModel>()
//
//    private var searchValue: String = ""
//
//    private val trackList = arrayListOf<Track>()
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var trackAdapter: TrackAdapter
//
//    private lateinit var textWatcher: TextWatcher
//
//    private lateinit var onTrackClickDebounce: (Track) -> Unit
//
//    private val internetBroadcastReceiver = InternetBroadcastReceiver()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentSearchBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        if (savedInstanceState !== null) {
//            searchValue = savedInstanceState.getString(SEARCH_QUERY, "")
//        }
//
//        onTrackClickDebounce = debounce<Track>(
//            CLICK_DEBOUNCE_DELAY,
//            viewLifecycleOwner.lifecycleScope,
//            false
//        ) { track ->
//            viewModel.addToHistory(track)
//            findNavController().navigate(
//                R.id.action_search_fragment_to_playerFragment,
//                PlayerFragment.createArgs(viewModel.provideTrackData(track))
//            )
//        }
//
//
//        binding.searchEditText.setText(searchValue)
//        viewModel.doSearch(searchValue)
//        viewModel.observeState().observe(viewLifecycleOwner) {
//            renderState(it)
//        }
//
//        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
//            showToast(toast)
//        }
//
//        // поисковая форма
//        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                viewModel.doSearch(searchValue)
//                true
//            }
//            false
//        }
//
//        binding.searchIconClear.setOnClickListener {
//            binding.searchEditText.setText("")
//            val inputMethodManager = requireActivity().getSystemService<InputMethodManager>()
//            inputMethodManager?.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
//            viewModel.setSearchText("")
//            viewModel.toggleHistory(true)
//        }
//
//        // обработчик фокуса на текстовое поле. Включаем кнопки только если есть фокус и пустой текст
//        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
//            viewModel.toggleHistory(hasFocus && binding.searchEditText.text.isNullOrEmpty())
//        }
//
//        textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                searchValue = s?.toString() ?: ""
//                viewModel.searchDebounce(
//                    changedText = searchValue
//                )
//                binding.searchIconClear.isVisible = searchValue.isNotEmpty()
//                if (searchValue.isEmpty()) {
//                    viewModel.toggleHistory(true)
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//            }
//        }
//        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }
//        recyclerView = binding.trackList
//
//        trackAdapter = TrackAdapter(trackList, onTrackClickDebounce)
//        binding.trackList.layoutManager = LinearLayoutManager(requireActivity())
//        binding.trackList.adapter = trackAdapter
//
//        binding.searchErrorRefresh.setOnClickListener {
//            viewModel.doSearch(searchValue)
//        }
//        binding.searchHistoryClear.setOnClickListener {
//            viewModel.clearHistory()
//        }
//    }
//
//    private fun renderState(state: SearchState) {
//        if (state.status == SearchState.ErrorStatus.LOADING) {
//            binding.progressBar.isVisible = true
//        } else {
//            binding.progressBar.isVisible = false
//        }
//        binding.searchHistoryTitle.isVisible = false
//        binding.searchHistoryClear.isVisible = false
//        binding.searchError.isVisible = false
//        binding.searchErrorRefresh.isVisible = false
//        when (state.status) {
//            SearchState.ErrorStatus.NONE -> {
//            }
//            SearchState.ErrorStatus.ERROR_NET -> {
//                binding.searchError.isVisible = true
//                binding.searchErrorRefresh.isVisible = true
//                binding.searchErrorImage.setImageResource(R.drawable.search_network_error)
//                binding.searchErrorText.text = getText(R.string.search_network_error)
//                binding.trackList.isVisible = false
//            }
//            SearchState.ErrorStatus.EMPTY_RESULT -> {
//                binding.searchError.isVisible = true
//                binding.searchErrorRefresh.isVisible = false
//                binding.searchErrorImage.setImageResource(R.drawable.search_nothing_found)
//                binding.searchErrorText.text = getText(R.string.search_nothing_found)
//                binding.trackList.isVisible = false
//            }
//            SearchState.ErrorStatus.LOADING -> {
//                binding.searchHistoryTitle.isVisible = false
//                binding.searchHistoryClear.isVisible = false
//                binding.searchError.isVisible = false
//                binding.searchErrorRefresh.isVisible = false
//                binding.trackList.isVisible = false
//            }
//            SearchState.ErrorStatus.SUCCESS -> {
//                binding.searchError.isVisible = false
//                binding.trackList.isVisible = true
//                trackList.clear()
//                trackList.addAll(state.trackList)
//                trackAdapter.notifyDataSetChanged()
//            }
//            SearchState.ErrorStatus.HISTORY -> {
//                binding.searchHistoryTitle.isVisible = true
//                binding.searchHistoryClear.isVisible = true
//                trackList.clear()
//                trackList.addAll(state.trackList.reversed())
//                trackAdapter.notifyDataSetChanged()
//                recyclerView.isVisible = true
//            }
//            SearchState.ErrorStatus.EMPTY_HISTORY -> {
//                binding.searchHistoryTitle.isVisible = false
//                binding.searchHistoryClear.isVisible = false
//                trackList.clear()
//                trackAdapter.notifyDataSetChanged()
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        @Suppress("DEPRECATION")
//        ContextCompat.registerReceiver(requireContext(), internetBroadcastReceiver,
//            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        try {
//            requireContext().unregisterReceiver(internetBroadcastReceiver)
//        } catch (e: IllegalArgumentException) {
//            // Ресивер не был зарегистрирован
//        }
//    }
//
//    private fun showToast(additionalMessage: String) {
//        Toast.makeText(requireActivity(), additionalMessage, Toast.LENGTH_LONG).show()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(SEARCH_QUERY, searchValue)
//    }
//
//    companion object {
//        private const val SEARCH_QUERY = "SEARCH_QUERY"
//
//        const val CLICK_DEBOUNCE_DELAY = 1000L
//    }
//}

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: SearchViewModel = koinViewModel()
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState()

    val currentQuery = remember { mutableStateOf("") }

    // Показ Toast при получении события
    LaunchedEffect (Unit) {
        viewModel.showToast.observeForever { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.trackClicks
            .onEach {
                Log.d("pls", "tohistory")
                viewModel.addToHistory(it)
            }
            .collect { track ->
                navController.navigate("player/${viewModel.provideTrackData(track)}")
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = currentQuery.value,
            onValueChange = {
                currentQuery.value = it
                viewModel.searchDebounce(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White),
            placeholder = { Text("Поиск") }
        )

        when (val state = uiState) {
            null -> {}
            else -> {
                when (state.status) {
                    SearchState.ErrorStatus.LOADING -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    SearchState.ErrorStatus.SUCCESS -> {
                        TrackList(state.trackList ?: emptyList()) { track ->
                            viewModel.onTrackClicked(track)
                        }
                    }
                    SearchState.ErrorStatus.ERROR_NET -> {
                        Text("Ошибка сети", modifier = Modifier.padding(16.dp))
                    }
                    SearchState.ErrorStatus.EMPTY_RESULT -> {
                        Text("Ничего не найдено", modifier = Modifier.padding(16.dp))
                    }
                    SearchState.ErrorStatus.HISTORY -> {
                        TrackList(state.trackList ?: emptyList()) { track ->
                            viewModel.onTrackClicked(track)
                        }
                    }
                    SearchState.ErrorStatus.EMPTY_HISTORY -> {
                        Text("История пуста", modifier = Modifier.padding(16.dp))
                    }
                    SearchState.ErrorStatus.NONE -> {}
                }
            }
        }

        Button(
            onClick = { viewModel.clearHistory() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Очистить историю")
        }
    }
}

@Composable
fun TrackList(tracks: List<Track>, onItemClick: (Track) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onClick = {
                    onItemClick(track)
                }
            )
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = dimensionResource(id = R.dimen.btn_margin), vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Обложка
        Image(
            painter = rememberAsyncImagePainter(track.poster),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )

        // Основная информация о треке
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Image(
                    painter = painterResource(id = R.drawable.circle),
                    contentDescription = null,
                    modifier = Modifier.size(4.dp)
                )

                Text(
                    text = track.trackTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Иконка стрелки
        Image(
            painter = painterResource(id = R.drawable.icon_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(id = R.dimen.settings_icon_height)),
            colorFilter = ColorFilter.tint(LocalContentColor.current)
        )
    }
}