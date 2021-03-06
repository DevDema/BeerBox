package com.andreadematteis.assignments.beerbox.view.fragments

import android.graphics.Bitmap
import com.andreadematteis.assignments.beerbox.model.Beer

data class BeerAdapterItem(
    val beer: Beer,
    var loading: Boolean = true,
    var image: Bitmap? = null
) {
    override fun hashCode(): Int = beer.id

    override fun equals(other: Any?): Boolean {
        if(other !is BeerAdapterItem) {
            return false
        }

        return beer == other.beer &&
                loading == other.loading
    }
}
