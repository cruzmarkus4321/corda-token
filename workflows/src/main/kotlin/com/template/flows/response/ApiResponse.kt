package com.template.flows.response


import com.google.gson.annotations.SerializedName


data class ApiResponse (
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: Rates
)
