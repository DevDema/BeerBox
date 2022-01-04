package com.satispay.assignment.beerbox.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Fermentation(
    @SerializedName("temp")
    @Expose
    val temp: Temp
)