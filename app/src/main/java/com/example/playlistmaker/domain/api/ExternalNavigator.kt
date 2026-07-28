package com.example.playlistmaker.domain.api

interface ExternalNavigator {
    fun openLink(link: String)

    fun shareText(text: String)

    fun openEmail(email: String, subject: String, message: String)
}
