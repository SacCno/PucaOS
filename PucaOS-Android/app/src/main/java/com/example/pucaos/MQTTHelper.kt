package com.example.pucaos

import android.R
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit

object MQTTHelper {
    private const val BROKER_URL = "d992db3a97704af2b6a05bc13d30c31e.s1.eu.hivemq.cloud"
    private const val USERNAME = "Phone-R"
    private const val PASSWORD = "M@u4wdZBsKuC3H5"

    private val connectMutex = Mutex()

    private val client by lazy {
        Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(BROKER_URL)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .buildAsync()
    }

    private suspend fun connect()
    {
        connectMutex.withLock {
            if(client.state.isConnected) return

            try {
                client.connectWith()
                    .simpleAuth()
                    .username(USERNAME)
                    .password(PASSWORD.toByteArray())
                    .applySimpleAuth()
                    .send()
                    .get(10, TimeUnit.SECONDS)
                Log.d("MQTT", "Connected to broker")
            } catch (e: Exception) {
                val msg = e.message ?: ""
                if(msg.contains("already connected", ignoreCase = true)) {
                    Log.e("MQTT", "Already connected")
                } else {
                    Log.e("MQTT", "Connection failed: $msg")
                    throw e
                }
            }
        }
    }


    fun observeTopic(topic: String): Flow<String> = callbackFlow {
        withContext(Dispatchers.IO) {
            try {
                connect()
                client.subscribeWith()
                    .topicFilter(topic)
                    .callback { publish ->
                        val payload = String(publish.payloadAsBytes, Charsets.UTF_8)
                        trySend(payload)
                    }
                    .send()
                    .get(5, TimeUnit.SECONDS)
            } catch (e: Exception) {
                Log.e("MQTT", "Failed to subscribe to $topic")
            }
        }

        awaitClose {
            try {
                client.unsubscribeWith()
                    .topicFilter(topic)
                    .send()
            } catch (e: Exception) {
                Log.e("MQTT", "Failed to unsubscribe from $topic")
            }
        }
    }



    suspend fun publish(topic: String, message: String, retain: Boolean = false): String {
        return withContext(Dispatchers.IO) {
            try {
                connect()
                client.publishWith()
                    .topic(topic)
                    .payload(message.toByteArray())
                    .retain(retain)
                    .send()
                    .get(5, TimeUnit.SECONDS)
                Log.d("MQTT", "Published to $topic: $message")
                "MQTT: Sent $message"
            } catch (e: Exception) {
                Log.e("MQTT", "Publish failed: ${e.message}")
                "MQTT Error: ${e.message}"
            }
        }
    }



    suspend fun request(topic: String, responseTopic: String, payload: String = "request"): String?
    {
        return withContext(Dispatchers.IO)
        {
            try
            {
                connect()

                var receivedMessage: String? = null
                val latch =java.util.concurrent.CountDownLatch(1)

                client.subscribeWith()
                    .topicFilter(responseTopic)
                    .callback { publish ->
                        receivedMessage = String(publish.payloadAsBytes)
                        latch.countDown()
                    }
                    .send()
                    .get(5, TimeUnit.SECONDS)

                client.publishWith()
                    .topic(topic)
                    .payload(payload.toByteArray())
                    .send()
                    .get(5, TimeUnit.SECONDS)

                val received = latch.await(8, TimeUnit.SECONDS)

                client.unsubscribeWith()
                    .topicFilter(responseTopic)
                    .send()
                    .get(5, TimeUnit.SECONDS)

                if(received) receivedMessage else null
            } catch (e: Exception)
            {
                Log.e("MQTT", "Request failed: $topic")
                null
            }
        }
    }


    suspend fun fetchMode() = MQTTHelper.request("/ph/command/mode", "/mc/command/mode")
}