package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Job

class ItunesNetworkClient(private val itunesApiService: ItunesApiService) : NetworkClient {
    private var currentJob: Job? = null

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return try {
            val response = itunesApiService.search(dto.query)
            response.apply { resultCode = 200 }
        } catch (_: Throwable) {
            Response().apply { resultCode = -1 }
        }

    }

    override fun cancelRequest() {
        currentJob?.cancel()
        currentJob = null
    }
}
