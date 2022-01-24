package com.andreadematteis.assignments.beerbox.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class MashTemp(
    @SerializedName("duration")
    @Expose
    val duration: Int,
    @SerializedName("temp")
    @Expose
    val temp: TempX
)