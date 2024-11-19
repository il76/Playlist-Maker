package com.il76.playlistmaker.data

import com.il76.playlistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}