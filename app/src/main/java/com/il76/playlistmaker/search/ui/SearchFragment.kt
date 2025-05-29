package com.il76.playlistmaker.search.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.il76.playlistmaker.R
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.ui.shared.ErrorImageText
import com.il76.playlistmaker.ui.shared.TrackList
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

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
    val uiState by viewModel.state.collectAsState()

    var currentQuery by rememberSaveable { mutableStateOf("") }

    // Показ Toast при получении события
    LaunchedEffect (Unit) {
        viewModel.showToast.observeForever { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.trackClicks
            .onEach {
                viewModel.addToHistory(it)
            }
            .collect { track ->
                navController.navigate("player/${viewModel.provideTrackData(track)}")
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchTextField(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.pdg_root), vertical = 8.dp),
            onSearchTextChanged = {
                currentQuery = it
                viewModel.searchDebounce(it)
            }
        )
        when (val state = uiState) {
            else -> {
                when (state.status) {
                    SearchState.ErrorStatus.LOADING -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    SearchState.ErrorStatus.SUCCESS -> {
                        TrackScreen(
                            tracks = state.trackList ?: emptyList(),
                            onTrackClick = { viewModel.onTrackClicked(it) },
                            modifier = Modifier
                        )
                    }
                    SearchState.ErrorStatus.ERROR_NET -> {
                        ErrorImageText(R.drawable.search_network_error, R.string.search_network_error)
                        Button(
                            onClick = {viewModel.doSearch(currentQuery)},
                            modifier = Modifier
                                .padding(16.dp).align(Alignment.CenterHorizontally)
                        ) {
                            Text(stringResource(R.string.search_refresh))
                        }
                    }
                    SearchState.ErrorStatus.EMPTY_RESULT -> {
                        ErrorImageText(R.drawable.search_nothing_found, R.string.search_nothing_found)
                    }
                    SearchState.ErrorStatus.HISTORY -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TrackScreen(
                                tracks = state.trackList ?: emptyList(),
                                onTrackClick = { viewModel.onTrackClicked(it) },
                                modifier = Modifier.weight(1f) // Занимает всё доступное место
                            )

                            Button(
                                onClick = { viewModel.clearHistory() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onBackground,
                                    contentColor = MaterialTheme.colorScheme.background,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(stringResource(R.string.search_history_clear))
                            }
                        }
                    }
                    SearchState.ErrorStatus.EMPTY_HISTORY -> {
                        //Text("История пуста", modifier = Modifier.padding(16.dp))
                    }
                    SearchState.ErrorStatus.NONE -> {}
                }
            }
        }


    }
}

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    hint: String = stringResource(id = R.string.button_search),
    onSearchTextChanged: (String) -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    val viewModel: SearchViewModel = koinViewModel()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = dimensionResource(id = R.dimen.pdg_root), vertical = 8.dp)
            .background(
                color = colorResource(id = R.color.search_edit_bg),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.icon_search),
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.settings_icon_height))
                    .padding(3.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.search_edit_main))
            )

            BasicTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    onSearchTextChanged(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .onFocusChanged {
                        if (it.isFocused && searchText.isEmpty()) {
                            viewModel.toggleHistory(true)
                        }
                    },
                singleLine = true,
                maxLines = 1,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(id = R.color.main_icon_fill)
                ),
                cursorBrush = SolidColor(colorResource(id = R.color.search_edit_main)),

                decorationBox = { innerTextField ->
                    if (searchText.isEmpty()) {
                        Text(
                            text = hint,
                            style = LocalTextStyle.current.copy(
                                color = colorResource(id = R.color.search_edit_main)
                            )
                        )
                    }
                    innerTextField()
                }
            )

            // Clear Icon
            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = {
                        searchText = ""
                        viewModel.setSearchText("")
                    },
                    modifier = Modifier.size(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_clear),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = colorResource(id = R.color.search_edit_main))
                    )
                }
            }
        }
    }
}


@Composable
fun TrackScreen(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier
) {
        TrackList(
            tracks = tracks.reversed(),
            onItemClick = onTrackClick,
            modifier = modifier
        )
}

