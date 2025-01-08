package com.il76.playlistmaker.sharing.api

import com.il76.playlistmaker.sharing.data.EmailData

interface SharingInteractor {
    fun share(url: String): String
    fun openTOS(url: String): String
    fun writeSupport(emailData: EmailData): String
}