package com.example.playlistmaker.sharing.domain.api

interface ResourceProvider {
    fun getAgreementLink(): String
    fun getShareText(): String
    fun getSupportEmail(): String
    fun getSupportSubject(): String
    fun getSupportMessage(): String
}