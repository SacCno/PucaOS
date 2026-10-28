package com.example.pucaos

import android.util.Log

object Plug {
    private val TOPIC = "/ph/command/plug"

    suspend fun toggle()
    {
        try {
            var response: String? = MQTTHelper.request("/ph/plug/status", "/mc/plug/status")

            response ?: return

            if(response.contains("on", ignoreCase = true)) {
                MQTTHelper.publish(TOPIC, "off")
            } else {
                MQTTHelper.publish(TOPIC, "on")
            }
        } catch (e: Exception) {
            Log.e("MQTT", "Toggle error: ${e.message}")
            throw e
        }
    }
}