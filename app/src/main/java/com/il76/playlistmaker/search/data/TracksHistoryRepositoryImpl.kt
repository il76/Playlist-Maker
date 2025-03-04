package com.il76.playlistmaker.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.il76.playlistmaker.search.domain.api.TracksHistoryRepository
import com.il76.playlistmaker.search.domain.models.Track

class TracksHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) :
    TracksHistoryRepository {

    private val trackListHistory = arrayListOf<Track>()

    override fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(TRACKS_SEARCH_HISTORY,null)
        val itemType = object : TypeToken<List<Track>>() {}.type

        val arrayList: ArrayList<Track> = if (json != null) {
            gson.fromJson(json, itemType)
        } else {
            ArrayList()
        }
        trackListHistory.clear()
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
        sharedPreferences.edit().putString(TRACKS_SEARCH_HISTORY, gson.toJson(trackListHistory)).apply()
    }

    companion object {
        const val TRACKS_SEARCH_HISTORY = "tracks_search_history"
    }

}