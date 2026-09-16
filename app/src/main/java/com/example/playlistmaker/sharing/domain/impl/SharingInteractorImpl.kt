package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val navigator: ExternalNavigator,
    private val resourceProvider: ResourceProvider
) : SharingInteractor {
    override fun openAgreement() {
        val link = resourceProvider.getAgreementLink()
        navigator.openLink(link)
    }

    override fun share() {
        val text = resourceProvider.getShareText()
        navigator.shareText(text)
    }

    override fun openSupport() {
        val email = resourceProvider.getSupportEmail()
        val subject = resourceProvider.getSupportSubject()
        val message = resourceProvider.getSupportMessage()
        navigator.openEmail(email, subject, message)
    }
}