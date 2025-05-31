package com.il76.playlistmaker.media.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.il76.playlistmaker.R
import org.koin.androidx.compose.koinViewModel

class MediaFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen(findNavController())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(navController: NavController) {
    val viewModel: MediaViewModel = koinViewModel()
    val context = LocalContext.current
    val backgroundColor = ContextCompat.getColor(context, R.color.background_secondary)
    val textColor = remember { ContextCompat.getColor(context, R.color.settings_text) }
    val currentTab by viewModel.currentTab.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.button_media),
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
        Column(modifier = Modifier.fillMaxSize().background(Color(backgroundColor)).padding(innerPadding)) {
            TabRow(
                selectedTabIndex = currentTab,
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
                                    color = Color(textColor),
                                ),
                                //fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.background(Color(backgroundColor)),
                    )
                }
            }
            when (currentTab) {
                0 -> FavoriteTracksScreen(navController)
                1 -> PlaylistsScreen(navController)
            }
        }
    }
}