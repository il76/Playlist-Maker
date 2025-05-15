package com.il76.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentSettingsBinding
import com.il76.playlistmaker.sharing.data.EmailData
import com.il76.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
//import com.il76.playlistmaker.ui.theme.LocalColors
import org.koin.androidx.compose.koinViewModel

class SettingsFragment: Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun render(state: SettingsState) {
        binding.SettingsThemeSwitcher.isChecked = state.isChecked
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }

        binding.SettingsShareApp.setOnClickListener {
            viewModel.shareApp(requireContext().getString(R.string.share_app_text))
        }

        binding.SettingsWriteSupport.setOnClickListener {
            viewModel.writeSupport(
                EmailData(
                    title = requireContext().getString(R.string.settings_email_subject),
                    subject = requireContext().getString(R.string.settings_email_subject),
                    sender = requireContext().getString(R.string.settings_email_from),
                    recipient = requireContext().getString(R.string.settings_email_recipient),
                )
            )
        }

        binding.SettingsUserAgreement.setOnClickListener {
            viewModel.openTOS(requireContext().getString(R.string.settings_ua_link))
        }

        //обрабатываем переключатель темы
        binding.SettingsThemeSwitcher.setOnCheckedChangeListener {_, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }

}

@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.observeState().observeAsState()
    val toastMessage by viewModel.observeShowToast().observeAsState(initial = null)

    val context = LocalContext.current


    // Обработка Toast
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            //.background(MaterialTheme.colors.background)
    ) {
        // Toolbar
//        TopAppBar(
//            title = { Text(stringResource(R.string.button_settings)) },
//            backgroundColor = MaterialTheme.colors.primarySurface
//        )

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

// Компонент переключателя
@Composable
private fun SwitchItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// Компонент кнопки настроек
@Composable
private fun SettingsButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit
) {
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = icon,
            contentDescription = null
        )
    }
}

//@Composable
//@Preview
//fun SettingsScreenPreview() {
//    SettingsScreen()
//}