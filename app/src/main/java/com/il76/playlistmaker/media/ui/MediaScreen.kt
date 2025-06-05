package com.il76.playlistmaker.media.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.il76.playlistmaker.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(navController: NavController) {
    val viewModel: MediaViewModel = koinViewModel()
    val context = LocalContext.current
    val backgroundColor = remember { ContextCompat.getColor(context, R.color.background_secondary) }
    val textColor = remember { ContextCompat.getColor(context, R.color.settings_text) }
    val buttonTextColor = remember { ContextCompat.getColor(context, R.color.background_secondary) }
    val buttonBackgroundColor = remember { ContextCompat.getColor(context, R.color.back_icon_fill) }

    val currentTab by viewModel.currentTab.collectAsState()
    val pagerState = rememberPagerState(initialPage = currentTab) {2}

    // Синхронизируем PagerState с ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectTab(pagerState.currentPage)
    }

    // Синхронизируем ViewModel с PagerState
    LaunchedEffect(currentTab) {
        launch {
            pagerState.animateScrollToPage(currentTab)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.button_media),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(textColor)
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(backgroundColor)
                ),
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().background(Color(backgroundColor)).padding(innerPadding)) {
            TabRow(
                selectedTabIndex = currentTab,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[currentTab])
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color(textColor))
                    )
                },
                divider = {
                    HorizontalDivider(modifier = Modifier.height(0.dp))
                },
                containerColor = Color(buttonBackgroundColor),
                contentColor = Color(buttonTextColor),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                val titles = listOf(
                    stringResource(R.string.media_tab_faforite_tracks),
                    stringResource(R.string.media_tab_playlists)
                )
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color(textColor),
                                ),
                            )
                        },
                        modifier = Modifier.background(Color(backgroundColor)),
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top) // Прижимаем контент к верху
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> TracksScreen(navController)
                    1 -> PlaylistsScreen(navController)
                }
            }
        }
    }
}