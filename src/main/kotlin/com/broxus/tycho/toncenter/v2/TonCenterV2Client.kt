package com.broxus.tycho.toncenter.v2

import com.broxus.tycho.toncenter.common.TychoCenterHttpClient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TonCenterV2Client(
    private val httpClient: TychoCenterHttpClient,
    private val basePath: String = "/toncenter/v2"
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    suspend fun getMasterchainInfo(): MasterchainInfoResponse {
        val response = httpClient.get("$basePath/getMasterchainInfo")
        return json.decodeFromString(response)
    }
    
    suspend fun getBlockHeader(params: BlockHeaderParams): BlockHeaderResponse {
        val queryParams = buildMap<String, String> {
            put("workchain", params.workchain.toString())
            put("shard", params.shard.toString())
            put("seqno", params.seqno.toString())
            params.rootHash?.let { put("root_hash", it.value) }
            params.fileHash?.let { put("file_hash", it.value) }
        }
        val response = httpClient.get("$basePath/getBlockHeader", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getShards(params: GetShardsParams): ShardsResponse {
        val queryParams = mapOf("seqno" to params.seqno.toString())
        val response = httpClient.get("$basePath/shards", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun detectAddress(params: DetectAddressParams): AddressFormsResponse {
        val queryParams = mapOf("address" to params.address)
        val response = httpClient.get("$basePath/detectAddress", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getAddressInformation(params: AccountParams): AddressInformationResponse {
        val queryParams = mapOf("address" to params.address.toString())
        val response = httpClient.get("$basePath/getAddressInformation", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getExtendedAddressInformation(params: AccountParams): ExtendedAddressInformationResponse {
        val queryParams = mapOf("address" to params.address.toString())
        val response = httpClient.get("$basePath/getExtendedAddressInformation", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getWalletInformation(params: AccountParams): WalletInformationResponse {
        val queryParams = mapOf("address" to params.address.toString())
        val response = httpClient.get("$basePath/getWalletInformation", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getTokenData(params: AccountParams): TokenData {
        val queryParams = mapOf("address" to params.address.toString())
        val response = httpClient.get("$basePath/getTokenData", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getTransactions(params: TransactionsParams): List<TonlibTransaction> {
        val queryParams = buildMap<String, String> {
            put("address", params.address.toString())
            put("limit", params.limit.toString())
            params.lt?.let { put("lt", it.toString()) }
            params.hash?.let { put("hash", it.value) }
            put("to_lt", params.toLt.toString())
        }
        val response = httpClient.get("$basePath/getTransactions", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getBlockTransactions(params: BlockTransactionsParams): GetBlockTransactionsResponse {
        val queryParams = buildMap<String, String> {
            put("workchain", params.workchain.toString())
            put("shard", params.shard.toString())
            put("seqno", params.seqno.toString())
            params.rootHash?.let { put("root_hash", it.value) }
            params.fileHash?.let { put("file_hash", it.value) }
            params.afterLt?.let { put("after_lt", it.toString()) }
            params.afterHash?.let { put("after_hash", it.value) }
            put("count", params.count.toString())
        }
        val response = httpClient.get("$basePath/getBlockTransactions", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getBlockTransactionsExt(params: BlockTransactionsParams): GetBlockTransactionsExtResponse {
        val queryParams = buildMap<String, String> {
            put("workchain", params.workchain.toString())
            put("shard", params.shard.toString())
            put("seqno", params.seqno.toString())
            params.rootHash?.let { put("root_hash", it.value) }
            params.fileHash?.let { put("file_hash", it.value) }
            params.afterLt?.let { put("after_lt", it.toString()) }
            params.afterHash?.let { put("after_hash", it.value) }
            put("count", params.count.toString())
        }
        val response = httpClient.get("$basePath/getBlockTransactionsExt", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun sendBoc(params: SendBocParams): ExtMsgInfoResponse {
        val body = json.encodeToString(params)
        val response = httpClient.post("$basePath/sendBoc", body)
        return json.decodeFromString(response)
    }
    
    suspend fun sendBocReturnHash(params: SendBocParams): ExtMsgInfoResponse {
        val body = json.encodeToString(params)
        val response = httpClient.post("$basePath/sendBocReturnHash", body)
        return json.decodeFromString(response)
    }
    
    suspend fun runGetMethod(params: RunGetMethodParams): RunGetMethodResponse {
        val body = json.encodeToString(params)
        val response = httpClient.post("$basePath/runGetMethod", body)
        return json.decodeFromString(response)
    }
    
    suspend fun jsonRpc(method: String, params: Any): String {
        val request = mapOf(
            "jsonrpc" to "2.0",
            "method" to method,
            "params" to params,
            "id" to 1
        )
        val body = json.encodeToString(request)
        return httpClient.post("$basePath/jsonRPC", body)
    }
}
