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
        val wrapper: MasterchainInfoResponseWrapper = json.decodeFromString(response)
        return wrapper.result
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
        val wrapper: AddressInformationResponseWrapper = json.decodeFromString(response)
        return wrapper.result
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
    
    /**
     * Sends a serialized message (BOC - Bag of Cells) to the blockchain.
     * 
     * @param params Parameters containing the serialized message
     * @return Information about the sent message including hash
     */
    suspend fun sendBoc(params: SendBocParams): ExtMsgInfoResponse {
        val body = json.encodeToString(params)
        val response = httpClient.post("$basePath/sendBoc", body)
        return json.decodeFromString(response)
    }
    
    /**
     * Sends a serialized message (BOC) to the blockchain and returns the hash.
     * 
     * Similar to sendBoc but specifically designed to return hash information.
     * 
     * @param params Parameters containing the serialized message
     * @return Information about the sent message including hash
     */
    suspend fun sendBocReturnHash(params: SendBocParams): ExtMsgInfoResponse {
        val body = json.encodeToString(params)
        val response = httpClient.post("$basePath/sendBocReturnHash", body)
        return json.decodeFromString(response)
    }
    
    /**
     * Executes a get method on a smart contract.
     * 
     * This method allows calling read-only methods on smart contracts to retrieve
     * data without modifying the contract state. The method is executed locally
     * and does not require sending a transaction.
     * 
     * @param params Parameters including contract address, method name, and stack arguments
     * @return Execution result including exit code, gas usage, and return stack
     */
    suspend fun runGetMethod(params: RunGetMethodParams): RunGetMethodResponse {
        val body = json.encodeToString(params)
        val response = httpClient.post("$basePath/runGetMethod", body)
        return json.decodeFromString(response)
    }
    
    /**
     * Performs a generic JSON-RPC call to the v2 API.
     * 
     * This method provides direct access to the underlying JSON-RPC protocol
     * for advanced use cases or methods not covered by the typed API methods.
     * 
     * @param method The JSON-RPC method name
     * @param params The method parameters
     * @return Raw JSON response as a string
     */
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
