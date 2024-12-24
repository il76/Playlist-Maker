package com.il76.playlistmaker.sharing.api

interface SharingInteractor {
    fun share(): String
    fun openTOS(): String
    fun writeSupport(): String
}