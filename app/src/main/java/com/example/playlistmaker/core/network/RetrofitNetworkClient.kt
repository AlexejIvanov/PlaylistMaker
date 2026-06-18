package com.example.playlistmaker.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.network.ITunesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Реализация сетевого клиента на Retrofit.
 * Отвечает за проверку связи и выполнение сетевых запросов.
 */
class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        // 1. Проверка наличия интернета
        if (!isConnected()) {
            return Response().apply { resultCode = -1 } // -1 — ошибка соединения
        }

        // 2. Проверка типа запроса (должен быть поиск треков)
        if (dto !is TrackSearchRequest) {
            return Response().apply { resultCode = 400 } // 400 — некорректный запрос
        }

        // 3. Выполнение синхронного запроса через Retrofit
        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply { resultCode = 500 }
            }
        }
    }

    /**
     * Проверяет наличие доступа к интернету (Wi-Fi, сотовая связь или Ethernet).
     */
    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
