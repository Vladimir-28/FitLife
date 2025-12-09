package mx.edu.utez.fitlife.data.model

import com.google.gson.annotations.SerializedName

data class ActivityDay(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("day")
    val day: String,
    @SerializedName("steps")
    val steps: Int,
    @SerializedName("distanceKm")
    val distanceKm: Float,
    @SerializedName("activeTime")
    val activeTime: String
)
