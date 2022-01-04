package com.satispay.assignment.beerbox.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Hop(
    @SerializedName("add")
    @Expose
    val add: String,
    @SerializedName("amount")
    @Expose
    val amount: Amount,
    @SerializedName("attribute")
    @Expose
    val attribute: String,
    @SerializedName("name")
    @Expose
    val name: String
)