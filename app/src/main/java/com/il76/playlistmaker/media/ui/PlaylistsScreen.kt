package com.il76.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.il76.playlistmaker.R
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.ui.shared.ErrorImageText
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(navController: NavController) {
    val viewModel: PlaylistsViewModel = koinViewModel()
    val playlists by viewModel.playlists.collectAsState()
    val context = LocalContext.current
    val buttonTextColor = remember { ContextCompat.getColor(context, R.color.background_secondary) }
    val buttonBackgroundColor = remember { ContextCompat.getColor(context, R.color.back_icon_fill) }

    LaunchedEffect(Unit) {
        viewModel.loadPlaylists()
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                navController.navigate(R.id.fragment_playlistadd)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(buttonBackgroundColor),
                contentColor = Color(buttonTextColor),
            ),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.new_playlist))
        }
        when (val data = playlists) {
            null -> {
                ErrorImageText(R.drawable.search_nothing_found, R.string.media_empty_playlists)
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Фиксируем 2 колонки
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(data.size) { index ->
                        val playlist = data[index]
                        PlaylistItem(playlist = playlist, navController)
                    }
                }
            }
        }




    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundColor = remember { ContextCompat.getColor(context, R.color.background_secondary) }
    val textColor = remember { ContextCompat.getColor(context, R.color.back_icon_fill) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(backgroundColor)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = null,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .background(Color(backgroundColor))
            .clickable(onClick = {
                val args = bundleOf("playlistid" to playlist.id)
                navController.navigate(R.id.fragment_playlist, args)
            }),

        ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(backgroundColor))
            ) {
                if (playlist.cover.isNotEmpty() && playlist.cover != "null") { //почему-то иногда прилетали
                    val imageUrl = playlist.cover
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = imageUrl,
                            placeholder = painterResource(id = R.drawable.search_cover_placeholder),
                            error = painterResource(id = R.drawable.search_cover_placeholder),
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.search_cover_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                }

            }

            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    color = Color(textColor),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = "${playlist.cnt} ${stringResource(R.string.playlist_tracks_cnt_description)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    color = Color(textColor),
                ),

                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}