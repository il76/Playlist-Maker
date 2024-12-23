package com.il76.playlistmaker.sharing.api

import android.net.Uri
import com.il76.playlistmaker.sharing.data.EmailData

interface ExternalNavigator {
    fun share(uri: Uri)
    fun openTOS(uri: Uri)
    fun writeSupport(emailData: EmailData)
}