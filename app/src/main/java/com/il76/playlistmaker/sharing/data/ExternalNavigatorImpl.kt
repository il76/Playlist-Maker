package com.il76.playlistmaker.sharing.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.widget.Toast
import com.il76.playlistmaker.R
import com.il76.playlistmaker.sharing.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun share(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_SUBJECT, uri)
        intent.setType("text/plain")
        if (intent.resolveActivity(context.packageManager) != null) {
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, context.getString(R.string.action_not_supported), Toast.LENGTH_SHORT).show()
        }
    }

    override fun openTOS(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        // intent.resolveActivity(packageManager) != null // не работает?
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.action_not_supported), Toast.LENGTH_SHORT).show()
        }
    }

    override fun writeSupport(emailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            // type = "*/*"
            setData (Uri.parse("mailto:")) //нам нужны только почтовые приложения
            putExtra(Intent.EXTRA_EMAIL, arrayOf("\"" + emailData.recipient + "\""))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.title)
            setFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.action_not_supported), Toast.LENGTH_SHORT).show()
        }
    }
}