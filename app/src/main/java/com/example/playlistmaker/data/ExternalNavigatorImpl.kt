package com.example.playlistmaker.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun openLink(link: String) {
        val url = link.toUri()
        val agreementIntent =
            Intent(Intent.ACTION_VIEW, url)

        context.startActivity(agreementIntent)
    }

    override fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        context.startActivity(shareIntent)
    }

    override fun openEmail(email: String, subject: String, message: String) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(supportIntent)
    }
}