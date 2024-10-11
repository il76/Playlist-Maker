package com.il76.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.il76.playlistmaker.App.Companion.TRACKS_SEARCH_HISTORY

data class TrackSearchHistory(val sp: SharedPreferences) {

    private fun saveToPreferences() {
        sp.edit().putString(TRACKS_SEARCH_HISTORY, Gson().toJson(App.instance.trackListHistory)).apply()
    }

    fun loadFromPreferences() {
        val json = sp.getString(TRACKS_SEARCH_HISTORY,null)
        Log.i("pls", json.toString())
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type

        val arrayList: ArrayList<Track> = if (json != null) {
            Gson().fromJson(json, itemType)
        } else {
            ArrayList()
        }
        App.instance.trackListHistory.addAll(arrayList)
    }

    fun clear() {
        App.instance.trackListHistory.clear()
        saveToPreferences()
    }

    fun addElement(track: Track) {
        val iterator = App.instance.trackListHistory.iterator()
        // ищем элемент в истории, если такой есть - удаляем, чтобы потом добавить
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element.trackId == track.trackId) {
                iterator.remove()
            }
        }
        App.instance.trackListHistory.add(track)
        if (App.instance.trackListHistory.size > 10) {
            App.instance.trackListHistory.removeAt(0)
        }
        saveToPreferences()
    }
}