package com.example.playlistmaker.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.network.ITunesApiService

/**
 * Реализация сетевого клиента на Retrofit.
 * Отвечает за проверку связи и выполнение сетевых запросов.
 */
class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApiService
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        // 1. Проверка наличия интернета
        if (!isConnected()) {
            return Response().apply { resultCode = -1 } // -1 — ошибка соединения
        }

        // 2. Проверка типа запроса (должен быть поиск треков)
        if (dto !is TrackSearchRequest) {
            return Response().apply { resultCode = 400 } // 400 — некорректный запрос
        }

        // 3. Выполнение синхронного запроса через Retrofit
        return try {
            val response = iTunesService.search(dto.expression).execute()
            val body = response.body()

            // Если тело ответа есть, ставим код ответа сервера, иначе возвращаем пустой Response с кодом
            body?.apply { resultCode = response.code() } ?: Response().apply { resultCode = response.code() }
        } catch (e: Exception) {
            Response().apply { resultCode = 500 } // 500 — ошибка сервера или исключение
        }
    }

    /**
     * Проверяет наличие доступа к интернету (Wi-Fi, сотовая связь или Ethernet).
     */
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

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