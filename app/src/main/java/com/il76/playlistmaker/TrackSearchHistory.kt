package com.il76.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.il76.playlistmaker.App.Companion.TRACKS_SEARCH_HISTORY

data class TrackSearchHistory(val sp: SharedPreferences) {

    fun put() {
        sp.edit().putString(TRACKS_SEARCH_HISTORY, Gson().toJson(App.instance.trackListHistory)).apply()
    }

    fun get(): ArrayList<Track> {
        val str = sp.getString(TRACKS_SEARCH_HISTORY,"")
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        val tl = Gson().fromJson<ArrayList<Track>>(str, TypeToken::class.java)
        return tl
    }

    fun clear() {
        App.instance.trackListHistory.clear()
        put()
    }
}
