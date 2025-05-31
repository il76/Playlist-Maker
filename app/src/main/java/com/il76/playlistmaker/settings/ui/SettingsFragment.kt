package com.il76.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.il76.playlistmaker.R
import com.il76.playlistmaker.sharing.data.EmailData
import org.koin.androidx.compose.koinViewModel

class SettingsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.observeState().observeAsState()
    val toastMessage by viewModel.observeShowToast().observeAsState(initial = null)

    val context = LocalContext.current
    val backgroundColor = ContextCompat.getColor(context, R.color.background_secondary)
    val textColor = remember { ContextCompat.getColor(context, R.color.settings_text) }

    // Обработка Toast
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.button_settings),
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
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(backgroundColor))
        ) {
            // Переключатель темы
            SwitchItem(
                text = stringResource(R.string.settings_button_darktheme),
                checked = state?.isChecked ?: false,
                onCheckedChange = { isChecked ->
                    viewModel.switchTheme(isChecked)
                }
            )

            // Кнопка "Поделиться приложением"
            val shareText = stringResource(R.string.share_app_text)
            SettingsButton(
                text = stringResource(R.string.settings_button_share),
                icon = painterResource(R.drawable.icon_share),
                onClick = { viewModel.shareApp(shareText) }
            )

            // Кнопка "Написать в поддержку"
            val emailTitle = stringResource(R.string.settings_email_subject)
            val emailSubject = stringResource(R.string.settings_email_subject)
            val emailSender = stringResource(R.string.settings_email_from)
            val emailRecipient = stringResource(R.string.settings_email_recipient)
            SettingsButton(
                text = stringResource(R.string.settings_button_support),
                icon = painterResource(R.drawable.icon_support),
                onClick = {
                    viewModel.writeSupport(
                        EmailData(
                            title = emailTitle,
                            subject = emailSubject,
                            sender = emailSender,
                            recipient = emailRecipient
                        )
                    )
                }
            )

            // Кнопка "Пользовательское соглашение"
            val uaLink = stringResource(R.string.settings_ua_link)
            SettingsButton(
                text = stringResource(R.string.settings_button_user_agreement),
                icon = painterResource(R.drawable.icon_arrow_right),
                onClick = { viewModel.openTOS(uaLink) }
            )
        }
    }

}

// Компонент переключателя
@Composable
private fun SwitchItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val textColor = remember { ContextCompat.getColor(context, R.color.settings_text) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(textColor)
            )
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(width = 40.dp, height = 24.dp),
            thumbContent = {
                val thumbColor = if (checked) Color(0xFF3772E7) else Color(0xFFAEAFB4)
                CircleIcon(color = thumbColor, size = 16.dp)
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color(0xFF3772E7).copy(alpha = 0.3f),
                uncheckedTrackColor = Color(0xFFE6E8EB),
                checkedThumbColor = Color.Transparent,
                uncheckedThumbColor = Color.Transparent
            )
        )
    }
}
@Composable
private fun CircleIcon(color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color = color, shape = CircleShape)
    )
}

// Компонент кнопки настроек
@Composable
private fun SettingsButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val textColor = remember { ContextCompat.getColor(context, R.color.settings_text) }
    val tintColor = remember { ContextCompat.getColor(context, R.color.settings_icon_fill) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(textColor)
            )
        )
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color(tintColor)
        )
    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    SettingsScreen()
}