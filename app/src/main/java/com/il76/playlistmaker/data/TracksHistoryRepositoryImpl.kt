package com.il76.playlistmaker.data

import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken
import com.il76.playlistmaker.Creator
import com.il76.playlistmaker.domain.api.TracksHistoryRepository
import com.il76.playlistmaker.domain.models.Track

class TracksHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : TracksHistoryRepository {

    private val trackListHistory = arrayListOf<Track>()

    override fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(TRACKS_SEARCH_HISTORY,null)
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type

        val arrayList: ArrayList<Track> = if (json != null) {
            Creator.provideGson().fromJson(json, itemType)
        } else {
            ArrayList()
        }
        trackListHistory.addAll(arrayList)
        return trackListHistory
    }

    override fun addTrack(track: Track) {
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
        saveHistory()
    }

    override fun clearHistory() {
        trackListHistory.clear()
        saveHistory()
    }

    override fun saveHistory() {
        sharedPreferences.edit().putString(TRACKS_SEARCH_HISTORY, Creator.provideGson().toJson(trackListHistory)).apply()
    }

    companion object {
        const val TRACKS_SEARCH_HISTORY = "tracks_search_history"
    }

}