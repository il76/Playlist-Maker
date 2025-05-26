package com.il76.playlistmaker.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.il76.playlistmaker.R

@Composable
fun ErrorImageText(imageResourceId: Int, stringResourceId: Int) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(stringResourceId),
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
@Preview
fun ErrorImageTextPreview() {
    ErrorImageText(
        imageResourceId = R.drawable.search_nothing_found, // Пример изображения
        stringResourceId = R.string.search_nothing_found // Пример текста
    )
}
