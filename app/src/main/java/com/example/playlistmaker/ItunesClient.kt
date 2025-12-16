package com.example.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ItunesClient {
    private const val ITUNES_BASE_URL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val itunesApiService: ITunesApiService = retrofit.create(ITunesApiService::class.java)
}