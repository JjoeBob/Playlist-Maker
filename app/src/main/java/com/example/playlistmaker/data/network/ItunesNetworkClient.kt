package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItunesNetworkClient : NetworkClient {
    private val baseUrl = "https://itunes.apple.com/"

    private var currentCall: Call<TracksSearchResponse>? = null

    private val client = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    private val itunesService: ItunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    override fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            try {
                val call = itunesService.search(dto.query)
                currentCall = call
                val resp = call.execute()
                val body = resp.body() ?: Response()

                body.apply { resultCode = resp.code() }
            } catch (_: Exception) {
                Response().apply { resultCode = -1 }
            }

        } else {
            Response().apply { resultCode = 400 }
        }
    }

    override fun cancelRequest() {
        currentCall?.cancel()
        currentCall = null
    }
}