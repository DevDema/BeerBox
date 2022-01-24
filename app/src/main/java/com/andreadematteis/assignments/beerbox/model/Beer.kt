package com.andreadematteis.assignments.beerbox.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Beer(
    @SerializedName("abv")
    @Expose
    val abv: Double,
    @SerializedName("attenuation_level")
    @Expose
    val attenuationLevel: Double,
    @SerializedName("boil_volume")
    @Expose
    val boilVolume: BoilVolume,
    @SerializedName("brewers_tips")
    @Expose
    val brewersTips: String,
    @SerializedName("contributed_by")
    @Expose
    val contributedBy: String,
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("ebc")
    @Expose
    val ebc: Float,
    @SerializedName("first_brewed")
    @Expose
    val firstBrewed: String,
    @SerializedName("food_pairing")
    @Expose
    val foodPairing: List<String>,
    @SerializedName("ibu")
    @Expose
    val ibu: Double,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("image_url")
    @Expose
    val imageUrl: String?,
    @SerializedName("ingredients")
    @Expose
    val ingredients: Ingredients,
    @SerializedName("method")
    @Expose
    val method: Method,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("ph")
    @Expose
    val ph: Double,
    @SerializedName("srm")
    @Expose
    val srm: Double,
    @SerializedName("tagline")
    @Expose
    val tagline: String,
    @SerializedName("target_fg")
    @Expose
    val targetFg: Int,
    @SerializedName("target_og")
    @Expose
    val targetOg: Double,
    @SerializedName("volume")
    @Expose
    val volume: Volume
)