package com.broxus.tycho.toncenter.common

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TychoCenterHttpClient(
    private val baseUrl: String,
    private val enableLogging: Boolean = false,
    private val connectTimeoutMs: Long = 30_000,
    private val readTimeoutMs: Long = 60_000
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
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
    
    suspend fun get(path: String, queryParams: Map<String, String> = emptyMap()): String {
        val urlBuilder = HttpUrl.parse("$baseUrl$path")?.newBuilder()
            ?: throw IllegalArgumentException("Invalid URL: $baseUrl$path")
        
        queryParams.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value)
        }
        
        val request = Request.Builder()
            .url(urlBuilder.build())
            .get()
            .build()
        
        return executeRequest(request)
    }
    
    suspend fun post(path: String, body: String, contentType: String = "application/json"): String {
        val requestBody = body.toRequestBody(contentType.toMediaType())
        
        val request = Request.Builder()
            .url("$baseUrl$path")
            .post(requestBody)
            .build()
        
        return executeRequest(request)
    }
    
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
    
    fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}

class HttpException(val code: Int, message: String) : Exception("HTTP $code: $message")
