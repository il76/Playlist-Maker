package com.il76.playlistmaker.search.data.network

import com.il76.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}