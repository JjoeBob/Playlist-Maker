package com.example.playlistmaker.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ResourceProvider

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getAgreementLink() = context.getString(R.string.agreement_link)

    override fun getShareText() = context.getString(R.string.share_link)

    override fun getSupportEmail() = context.getString(R.string.support_email)
    override fun getSupportSubject() = context.getString(R.string.support_subject)
    override fun getSupportMessage() = context.getString(R.string.support_message)
}