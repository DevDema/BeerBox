package com.andreadematteis.assignments.beerbox.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Method(
    @SerializedName("fermentation")
    @Expose
    val fermentation: Fermentation,
    @SerializedName("mash_temp")
    @Expose
    val mashTemp: List<MashTemp>,
    @SerializedName("twist")
    @Expose
    val twist: String
)