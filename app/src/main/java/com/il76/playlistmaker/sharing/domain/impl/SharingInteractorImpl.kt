package com.il76.playlistmaker.sharing.domain.impl

import android.content.Context
import com.il76.playlistmaker.R
import com.il76.playlistmaker.sharing.api.ExternalNavigator
import com.il76.playlistmaker.sharing.api.SharingInteractor
import com.il76.playlistmaker.sharing.data.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator): SharingInteractor {
    override fun share(url: String): String {
        return externalNavigator.share(url)
    }

    override fun openTOS(url: String): String {
        return externalNavigator.openTOS(url)
    }

    override fun writeSupport(emailData: EmailData): String {
        return externalNavigator.writeSupport(emailData)
    }
}