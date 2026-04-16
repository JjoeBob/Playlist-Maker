package com.example.playlistmaker.network

import androidx.lifecycle.ViewModelProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ItunesNetworkClient {
    private const val baseUrl = "https://itunes.apple.com"

    val itunesApi: ItunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }
}