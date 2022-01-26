package com.andreadematteis.assignments.beerbox.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Beer(
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("first_brewed")
    @Expose
    val firstBrewed: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("image_url")
    @Expose
    val imageUrl: String?,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("tagline")
    @Expose
    val tagline: String,
): Parcelable