package com.il76.playlistmaker.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.il76.playlistmaker.R

class InternetBroadcastReceiver: BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != "android.net.conn.CONNECTIVITY_CHANGE") {
            return
        }
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val hasInternet = isNetworkAvailable(connectivityManager)

        if (!hasInternet) {
            Toast.makeText(
                context,
                context.getString(R.string.no_internet_connection),
                Toast.LENGTH_LONG
            ).show()
        } else {
//            Toast.makeText(
//                context,
//                context.getString(R.string.internet_connection_restored),
//                Toast.LENGTH_SHORT
//            ).show()
        }

    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            // Для старых версий (до Android 6.0)
            @Suppress("DEPRECATION")
            val activeNetwork = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return activeNetwork.isConnected
        }
    }
}