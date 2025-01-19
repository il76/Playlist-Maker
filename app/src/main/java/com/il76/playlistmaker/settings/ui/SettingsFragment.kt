package com.il76.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentSettingsBinding
import com.il76.playlistmaker.sharing.data.EmailData
import org.koin.androidx.viewmodel.ext.android.viewModel

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