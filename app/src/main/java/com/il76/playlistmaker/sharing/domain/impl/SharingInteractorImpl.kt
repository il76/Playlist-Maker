package com.il76.playlistmaker.sharing.domain.impl

import android.content.Context
import android.net.Uri
import com.il76.playlistmaker.R
import com.il76.playlistmaker.sharing.api.ExternalNavigator
import com.il76.playlistmaker.sharing.api.SharingInteractor
import com.il76.playlistmaker.sharing.data.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator, private val context: Context): SharingInteractor {
    override fun share() {
        externalNavigator.share(getShareLink())
    }

    override fun openTOS() {
        externalNavigator.openTOS(getTOSLink())
    }

    override fun writeSupport() {
        val emailData = EmailData(
            title = context.getString(R.string.settings_email_subject),
            subject = context.getString(R.string.settings_email_subject),
            sender = context.getString(R.string.settings_email_from),
            recipient = context.getString(R.string.settings_email_recipient),
        )
        externalNavigator.writeSupport(emailData)
    }

    private fun getShareLink(): Uri {
        return Uri.parse(context.getString(R.string.share_app_text))
    }

    private fun getTOSLink(): Uri {
        return Uri.parse(context.getString(R.string.settings_ua_link))
    }
}