package com.satispay.assignment.beerbox.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Amount(
    @SerializedName("unit")
    @Expose
    val unit: String,
    @SerializedName("value")
    @Expose
    val value: Float
)