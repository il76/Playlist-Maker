package com.il76.playlistmaker.domain.api

import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken
import com.il76.playlistmaker.Creator
import com.il76.playlistmaker.data.TracksHistoryRepositoryImpl.Companion.TRACKS_SEARCH_HISTORY
import com.il76.playlistmaker.domain.models.Track

data class TrackSearchHistory(val sp: SharedPreferences) {
    val trackListHistory = arrayListOf<Track>()
    init {
        loadFromPreferences()
    }


    private fun saveToPreferences() {
        sp.edit().putString(TRACKS_SEARCH_HISTORY, Creator.provideGson().toJson(trackListHistory)).apply()
    }

    private fun loadFromPreferences() {
        val json = sp.getString(TRACKS_SEARCH_HISTORY,null)
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type

        val arrayList: ArrayList<Track> = if (json != null) {
            Creator.provideGson().fromJson(json, itemType)
        } else {
            ArrayList()
        }
        trackListHistory.addAll(arrayList)
    }

    fun clear() {
        trackListHistory.clear()
        saveToPreferences()
    }

    fun addElement(track: Track) {
        val iterator = trackListHistory.iterator()
        // ищем элемент в истории, если такой есть - удаляем, чтобы потом добавить
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element.trackId == track.trackId) {
                iterator.remove()
            }
        }
        trackListHistory.add(track)
        if (trackListHistory.size > 10) {
            trackListHistory.removeAt(0)
        }
        saveToPreferences()
    }
}