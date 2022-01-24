package com.andreadematteis.assignments.beerbox.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Ingredients(
    @SerializedName("hops")
    @Expose
    val hops: List<Hop>,
    @SerializedName("malt")
    @Expose
    val malt: List<Malt>,
    @SerializedName("yeast")
    @Expose
    val yeast: String
)