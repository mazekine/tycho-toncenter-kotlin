package com.broxus.tycho.toncenter

import com.broxus.tycho.toncenter.common.TychoCenterHttpClient
import com.broxus.tycho.toncenter.v2.TonCenterV2Client
import com.broxus.tycho.toncenter.v3.TonCenterV3Client

/**
 * Main entry point for accessing both v2 and v3 APIs of the Tycho TonCenter service.
 * 
 * This client provides a unified interface to interact with the TonCenter blockchain API,
 * supporting both the legacy JSON-RPC based v2 API and the modern REST-based v3 API.
 * The client handles HTTP connections, request/response serialization, and resource management.
 * 
 * ## Usage Example
 * ```kotlin
 * val client = TychoCenterClient(
 *     baseUrl = "https://toncenter-testnet.tychoprotocol.com",
 *     enableLogging = true
 * )
 * 
 * try {
 *     // Use v2 API (JSON-RPC)
 *     val masterchainInfo = client.v2.getMasterchainInfo()
 *     
 *     // Use v3 API (REST)
 *     val transactions = client.v3.getTransactions(
 *         TransactionsRequest(mcSeqno = 4488500u, limit = 10u)
 *     )
 * } finally {
 *     client.close()
 * }
 * ```
 * 
 * ## API Version Differences
 * - **v2**: JSON-RPC based API with response wrappers containing `ok` and `result` fields
 * - **v3**: REST-based API with direct JSON responses and snake_case field names
 * 
 * @param baseUrl The base URL of the TonCenter API endpoint
 * @param enableLogging Whether to enable HTTP request/response logging for debugging
 * @param connectTimeoutMs Connection timeout in milliseconds
 * @param readTimeoutMs Read timeout in milliseconds
 */
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
    
    /** Client for accessing the v2 JSON-RPC API endpoints */
    val v2 = TonCenterV2Client(httpClient)
    
    /** Client for accessing the v3 REST API endpoints */
    val v3 = TonCenterV3Client(httpClient)
    
    /**
     * Closes the HTTP client and releases all associated resources.
     * 
     * This method should be called when the client is no longer needed to properly
     * shut down the connection pool and executor services.
     */
    fun close() {
        httpClient.close()
    }
}

/**
 * Configuration data class for TychoCenterClient initialization.
 * 
 * @param baseUrl The base URL of the TonCenter API endpoint
 * @param enableLogging Whether to enable HTTP request/response logging
 * @param connectTimeoutMs Connection timeout in milliseconds
 * @param readTimeoutMs Read timeout in milliseconds
 */
data class TychoCenterConfig(
    val baseUrl: String = "https://toncenter.com",
    val enableLogging: Boolean = false,
    val connectTimeoutMs: Long = 30_000,
    val readTimeoutMs: Long = 60_000
)
