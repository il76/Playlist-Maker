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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.bundle.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.tabs.TabLayoutMediator
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentMediaBinding
import com.il76.playlistmaker.ui.shared.ErrorImageText
import org.koin.androidx.compose.koinViewModel

class MediaFragment: Fragment() {
    private lateinit var binding: FragmentMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mediaViewPager.adapter = MediaPagerAdapter(childFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.mediaTabLayout, binding.mediaViewPager) { tab, position ->
            when(position) {
                0 -> tab.text = requireContext().getString(R.string.media_tab_faforite_tracks)
                1 -> tab.text = requireContext().getString(R.string.media_tab_playlists)
            }
        }
        tabMediator.attach()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

    companion object {

        private const val ARGS_TRACKDATA = "track"

        fun createArgs(trackData: String): Bundle =
            bundleOf(ARGS_TRACKDATA to trackData)
    }
}


@Composable
fun MediaScreen(navController: NavController) {
    val viewModel: MediaViewModel = koinViewModel()
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TabRow(
            selectedTabIndex = currentTab,
            modifier = Modifier.background(MaterialTheme.colorScheme.onSecondary),
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

@Composable
fun FavoriteTracksScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        ErrorImageText(R.drawable.search_nothing_found, R.string.media_empty_tracks)
    }
}

@Composable
fun PlaylistsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                navController.navigate("playlistinfo")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.new_playlist))
        }
        ErrorImageText(R.drawable.search_nothing_found, R.string.media_empty_playlists)
    }
}
