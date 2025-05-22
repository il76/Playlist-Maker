package com.il76.playlistmaker.media.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaViewModel: ViewModel() {
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab

    fun selectTab(index: Int) {
        _currentTab.value = index
    }
}