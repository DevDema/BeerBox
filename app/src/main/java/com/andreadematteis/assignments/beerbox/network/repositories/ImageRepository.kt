package com.andreadematteis.assignments.beerbox.network.repositories

import com.andreadematteis.assignments.beerbox.network.ImageService
import com.andreadematteis.assignments.beerbox.utils.OpenForTesting
import okhttp3.ResponseBody

@OpenForTesting
class ImageRepository(
    private val service: ImageService
) {

    suspend fun getImage(imageStringUrl: String): ResponseBody = service.getImage(imageStringUrl)
}