package com.example.pucaos

import kotlinx.serialization.Serializable


@Serializable
data class Schedule(
    val count: Int = 0,
    val timeframes: List<Timeframe> = listOf()
)