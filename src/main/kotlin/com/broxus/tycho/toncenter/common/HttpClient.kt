package com.broxus.tycho.toncenter.common

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * HTTP client for making requests to the TonCenter API with coroutine support.
 * 
 * This client provides asynchronous HTTP operations using Kotlin coroutines and handles
 * JSON serialization/deserialization with support for blockchain-specific types like
 * BigInteger. It uses OkHttp as the underlying HTTP client with configurable timeouts
 * and optional request/response logging.
 * 
 * ## Key Features
 * - Coroutine-based async HTTP operations with proper cancellation support
 * - JSON serialization with custom serializers for blockchain types
 * - Configurable timeouts and logging
 * - Automatic error handling and HTTP status code validation
 * 
 * @param baseUrl The base URL for all HTTP requests
 * @param enableLogging Whether to enable detailed HTTP logging for debugging
 * @param connectTimeoutMs Connection timeout in milliseconds
 * @param readTimeoutMs Read timeout in milliseconds
 */
class TychoCenterHttpClient(
    private val baseUrl: String,
    private val enableLogging: Boolean = false,
    private val connectTimeoutMs: Long = 30_000,
    private val readTimeoutMs: Long = 60_000
) {
    /**
     * JSON configuration with custom serializers for blockchain-specific types.
     * 
     * Configured to ignore unknown JSON fields for forward compatibility and
     * includes contextual serializers for BigInteger values commonly used in
     * blockchain applications.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(BigIntegerSerializer)
        }
    }
    
    /**
     * OkHttp client configured with timeouts and optional logging.
     */
    private val client = OkHttpClient.Builder()
        .connectTimeout(connectTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
        .readTimeout(readTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
        .apply {
            if (enableLogging) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        .build()
    
    /**
     * Performs an asynchronous HTTP GET request with optional query parameters.
     * 
     * @param path The API endpoint path (will be appended to baseUrl)
     * @param queryParams Optional query parameters to include in the request
     * @return The response body as a string
     * @throws HttpException if the HTTP response indicates an error
     * @throws IOException if a network error occurs
     */
    suspend fun get(path: String, queryParams: Map<String, String> = emptyMap()): String {
        val urlBuilder = "$baseUrl$path".toHttpUrl().newBuilder()
        
        queryParams.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value)
        }
        
        val request = Request.Builder()
            .url(urlBuilder.build())
            .get()
            .build()
        
        return executeRequest(request)
    }
    
    /**
     * Performs an asynchronous HTTP POST request with a JSON body.
     * 
     * @param path The API endpoint path (will be appended to baseUrl)
     * @param body The request body as a JSON string
     * @param contentType The content type header (defaults to "application/json")
     * @return The response body as a string
     * @throws HttpException if the HTTP response indicates an error
     * @throws IOException if a network error occurs
     */
    suspend fun post(path: String, body: String, contentType: String = "application/json"): String {
        val requestBody = body.toRequestBody(contentType.toMediaType())
        
        val request = Request.Builder()
            .url("$baseUrl$path")
            .post(requestBody)
            .build()
        
        return executeRequest(request)
    }
    
    /**
     * Executes an HTTP request asynchronously using coroutines with proper cancellation support.
     * 
     * This method uses `suspendCancellableCoroutine` to bridge OkHttp's callback-based API
     * with Kotlin coroutines. It ensures that if the coroutine is cancelled, the underlying
     * HTTP call is also cancelled to prevent resource leaks.
     * 
     * @param request The HTTP request to execute
     * @return The response body as a string
     * @throws HttpException if the HTTP response indicates an error (non-2xx status code)
     * @throws IOException if a network error occurs or the response body is empty
     */
    private suspend fun executeRequest(request: Request): String = suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)
        
        continuation.invokeOnCancellation {
            call.cancel()
        }
        
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
            
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (!response.isSuccessful) {
                        continuation.resumeWithException(
                            HttpException(response.code, response.message)
                        )
                        return
                    }
                    
                    val responseBody = response.body?.string()
                        ?: throw IOException("Empty response body")
                    
                    continuation.resume(responseBody)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        })
    }
    
    /**
     * Closes the HTTP client and releases all associated resources.
     * 
     * This method shuts down the dispatcher's executor service and evicts all
     * connections from the connection pool. Should be called when the client
     * is no longer needed.
     */
    fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}

/**
 * Exception thrown when an HTTP request returns a non-successful status code.
 * 
 * @param code The HTTP status code
 * @param message The HTTP status message
 */
class HttpException(val code: Int, message: String) : Exception("HTTP $code: $message")
