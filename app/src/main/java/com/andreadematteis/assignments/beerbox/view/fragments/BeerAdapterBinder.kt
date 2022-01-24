package com.andreadematteis.assignments.beerbox.view.fragments

import android.graphics.Bitmap
import com.andreadematteis.assignments.beerbox.model.Beer

interface BeerAdapterBinder {

    fun loadImage(position: Int, beer: Beer)

    fun showDetails(beer: Beer, bitmap: Bitmap?)

    fun onNoBeerResults()

    fun onBeerResults(filteredValues: List<BeerAdapterItem>)
}
