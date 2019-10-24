package com.template.flows.response


import com.google.gson.annotations.SerializedName

data class Rates(
    @SerializedName("PHP")
    val php: Double
)
