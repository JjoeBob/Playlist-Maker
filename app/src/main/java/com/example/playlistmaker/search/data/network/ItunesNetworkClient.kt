package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call

class ItunesNetworkClient(private val itunesApiService: ItunesApiService) : NetworkClient {
    private var currentCall: Call<TracksSearchResponse>? = null

    override fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            try {
                val call = itunesApiService.search(dto.query)
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
