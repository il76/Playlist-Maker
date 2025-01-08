package com.il76.playlistmaker.sharing.api

import com.il76.playlistmaker.sharing.data.EmailData

interface ExternalNavigator {
    fun share(uri: String): String
    fun openTOS(uri: String): String
    fun writeSupport(emailData: EmailData): String
}