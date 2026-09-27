package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.ItunesApiService
import com.example.playlistmaker.search.data.network.ItunesNetworkClient
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

val dataModule = module {
    factory {
        Handler(Looper.getMainLooper())
    }

    factory {
        SimpleDateFormat("m:ss", Locale.getDefault())
    }

    factory {
        MediaPlayer()
    }

    factory {
        Gson()
    }

    single {
        androidContext().getSharedPreferences(
            SearchHistoryRepositoryImpl.SEARCH_HISTORY_PREFS,
            Context.MODE_PRIVATE
        )
    }

    single<ItunesApiService> {
        val baseUrl = "https://itunes.apple.com/"

        val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single<NetworkClient> {
        ItunesNetworkClient(get())
    }
}