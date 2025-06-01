package com.il76.playlistmaker.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.il76.playlistmaker.R
import com.il76.playlistmaker.search.domain.models.Track

@Composable
fun TrackList(
    tracks: List<Track>,
    onItemClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(top = 16.dp)) {
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
    val context = LocalContext.current
    val textColor = remember { ContextCompat.getColor(context, R.color.settings_text) }
    val textColor2 = remember { ContextCompat.getColor(context, R.color.settings_icon_fill) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Обложка
        Image(
            painter = rememberAsyncImagePainter(
                model = track.poster,
                placeholder = painterResource(id = R.drawable.search_cover_placeholder),
                error = painterResource(id = R.drawable.search_cover_placeholder),
            ),
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
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                ),
                color = Color(textColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                    ),
                    color = Color(textColor2),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 5.dp).weight(1f, fill = false)
                )

                Image(
                    painter = painterResource(id = R.drawable.circle),
                    contentDescription = null,
                    modifier = Modifier.size(3.dp)
                )

                Text(
                    text = track.trackTime,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                    ),
                    color = Color(textColor2),
                    modifier = Modifier.padding(start = 5.dp)
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