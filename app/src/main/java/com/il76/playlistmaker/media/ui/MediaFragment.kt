package com.il76.playlistmaker.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.il76.playlistmaker.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun MediaScreen(navController: NavController) {
    val viewModel: MediaViewModel = koinViewModel()
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TabRow(
            selectedTabIndex = currentTab,
            //modifier = Modifier.background(MaterialTheme.colorScheme.onSecondary),
            indicator = { tabPositions ->
                // Используем Box для кастомного индикатора
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[currentTab])
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.onSurface)
                )
            },
            divider = {
                // Убираем дефолтный разделитель
                HorizontalDivider(modifier = Modifier.height(0.dp))
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
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
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            //fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        when (currentTab) {
            0 -> FavoriteTracksScreen(navController)
            1 -> PlaylistsScreen(navController)
        }
    }
}
