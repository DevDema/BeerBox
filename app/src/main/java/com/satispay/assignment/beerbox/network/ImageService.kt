package com.satispay.assignment.beerbox.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface ImageService {

    @GET
    suspend fun getImage(@Url imageUrl: String): ResponseBody
}