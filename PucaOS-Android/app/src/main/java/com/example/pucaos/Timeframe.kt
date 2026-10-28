package com.example.pucaos

import kotlinx.serialization.Serializable

@Serializable
data class Timeframe(
    val minute_of_day: Int,
    val temperature: Int,
    val lamp_state: Int
)