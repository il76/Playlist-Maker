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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.il76.playlistmaker.R
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.ui.shared.ErrorImageText
import com.il76.playlistmaker.ui.shared.TrackList
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: SearchViewModel = koinViewModel()
    val context = LocalContext.current
    val backgroundColor = remember { ContextCompat.getColor(context, R.color.background_secondary) }
    val textColor = remember { ContextCompat.getColor(context, R.color.settings_text) }
    val buttonTextColor = remember { ContextCompat.getColor(context, R.color.background_secondary) }
    val buttonBackgroundColor = remember { ContextCompat.getColor(context, R.color.back_icon_fill) }
    val indicatorColor = remember { ContextCompat.getColor(context, R.color.progressbar_tint) }

    val uiState by viewModel.state.collectAsState()

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
                val args = bundleOf("trackData" to viewModel.provideTrackData(track))
                navController.navigate(R.id.playerFragment, args)
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.button_search),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(textColor)
                    )
                ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(backgroundColor)
                ),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background((Color(backgroundColor)))
                .padding(innerPadding)
        ) {
            SearchTextField(
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.pdg_root),
                    vertical = 8.dp
                ),
                onSearchTextChanged = {
                    viewModel.searchDebounce(it)
                }
            )
            when (val state = uiState) {
                else -> {
                    when (state.status) {
                        SearchState.ErrorStatus.LOADING -> CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 140.dp),
                            color = Color(indicatorColor),
                        )

                        SearchState.ErrorStatus.SUCCESS -> {
                            TrackScreen(
                                tracks = state.trackList ?: emptyList(),
                                onTrackClick = { viewModel.onTrackClicked(it) },
                                modifier = Modifier
                            )
                        }

                        SearchState.ErrorStatus.ERROR_NET -> {
                            ErrorImageText(
                                R.drawable.search_network_error,
                                R.string.search_network_error
                            )
                            Button(
                                onClick = { viewModel.doSearch() },
                                modifier = Modifier
                                    .padding(16.dp).align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(buttonBackgroundColor),
                                    contentColor = Color(buttonTextColor),
                            ),
                            ) {
                                Text(stringResource(R.string.search_refresh))
                            }
                        }

                        SearchState.ErrorStatus.EMPTY_RESULT -> {
                            ErrorImageText(
                                R.drawable.search_nothing_found,
                                R.string.search_nothing_found
                            )
                        }

                        SearchState.ErrorStatus.HISTORY -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.search_history_title),
                                    color = Color(textColor),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 19.sp
                                    ),
                                    modifier = Modifier.padding(18.dp)
                                )
                                TrackScreen(
                                    tracks = state.trackList ?: emptyList(),
                                    onTrackClick = { viewModel.onTrackClicked(it) },
                                    modifier = Modifier.weight(1f) // Занимает всё доступное место
                                )

                                Button(
                                    onClick = { viewModel.clearHistory() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(buttonBackgroundColor),
                                        contentColor = Color(buttonTextColor),
                                    ),
                                    shape = RoundedCornerShape(54.dp),
                                    modifier = Modifier
                                        .padding(24.dp)
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
}

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    hint: String = stringResource(id = R.string.button_search),
    onSearchTextChanged: (String) -> Unit
) {
    val viewModel: SearchViewModel = koinViewModel()
    val context = LocalContext.current
    val cursorColor = remember { ContextCompat.getColor(context, R.color.background_main) }
    val searchBackgroundColor = remember { ContextCompat.getColor(context, R.color.search_edit_bg) }
    val currentQuery by viewModel.currentQuery

    Box (
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                color = Color(searchBackgroundColor),
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
                value = currentQuery,
                onValueChange = {
                    onSearchTextChanged(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .onFocusChanged {
                        if (it.isFocused && currentQuery.isEmpty()) {
                            viewModel.toggleHistory(true)
                        }
                    },
                singleLine = true,
                maxLines = 1,
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    color = colorResource(id = R.color.main_icon_fill),
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(Color(cursorColor)),


                decorationBox = { innerTextField ->
                    if (currentQuery.isEmpty()) {
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = colorResource(id = R.color.search_edit_main),
                                lineHeight = 24.sp
                            )
                        )
                    }
                    innerTextField()
                }
            )

            // Clear Icon
            if (currentQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
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

