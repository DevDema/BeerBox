package com.satispay.assignment.beerbox.network

import okhttp3.ResponseBody

class ImageRepository(
    private val service: ImageService
) {

    suspend fun getImage(imageStringUrl: String): ResponseBody = service.getImage(imageStringUrl)
}