package com.satispay.assignment.beerbox

import android.graphics.Bitmap
import com.satispay.assignment.beerbox.model.Beer

interface BeerAdapterBinder {

    fun loadImage(position: Int, urlString: String)

    fun showDetails(beer: Beer, bitmap: Bitmap?)
}
