package com.broxus.tycho.toncenter

import com.broxus.tycho.toncenter.common.TychoCenterHttpClient
import com.broxus.tycho.toncenter.v2.TonCenterV2Client
import com.broxus.tycho.toncenter.v3.TonCenterV3Client

class TychoCenterClient(
    baseUrl: String = "https://toncenter.com",
    enableLogging: Boolean = false,
    connectTimeoutMs: Long = 30_000,
    readTimeoutMs: Long = 60_000
) {
    private val httpClient = TychoCenterHttpClient(
        baseUrl = baseUrl,
        enableLogging = enableLogging,
        connectTimeoutMs = connectTimeoutMs,
        readTimeoutMs = readTimeoutMs
    )
    
    val v2 = TonCenterV2Client(httpClient)
    val v3 = TonCenterV3Client(httpClient)
    
    fun close() {
        httpClient.close()
    }
}

data class TychoCenterConfig(
    val baseUrl: String = "https://toncenter.com",
    val enableLogging: Boolean = false,
    val connectTimeoutMs: Long = 30_000,
    val readTimeoutMs: Long = 60_000
)
