package com.satispay.assignment.beerbox

import android.graphics.Bitmap
import com.satispay.assignment.beerbox.model.Beer

interface BeerAdapterBinder {

    fun loadImage(beer: Beer)

    fun showDetails(beer: Beer, bitmap: Bitmap?)

    fun onNoBeerResults()

    fun onBeerResults(filteredValues: List<BeerAdapterItem>)
}
