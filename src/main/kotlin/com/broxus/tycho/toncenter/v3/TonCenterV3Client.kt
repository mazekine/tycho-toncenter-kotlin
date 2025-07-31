package com.broxus.tycho.toncenter.v3

import com.broxus.tycho.toncenter.common.TychoCenterHttpClient
import kotlinx.serialization.json.Json

/**
 * Client for accessing the TonCenter v3 REST API.
 * 
 * The v3 API is a modern REST-based interface that provides direct JSON responses
 * without wrapper objects. It uses snake_case field names in responses that are
 * automatically mapped to camelCase in Kotlin data classes using @SerialName annotations.
 * This API offers more efficient filtering and pagination capabilities compared to v2.
 * 
 * ## Key Features
 * - REST-based API with direct JSON responses
 * - Advanced filtering capabilities with multiple parameters
 * - Efficient pagination with limit/offset
 * - Snake_case to camelCase field mapping
 * - Support for complex queries with multiple filters
 * 
 * ## Usage Example
 * ```kotlin
 * val client = TonCenterV3Client(httpClient)
 * 
 * // Get masterchain information
 * val masterchainInfo = client.getMasterchainInfo()
 * 
 * // Get transactions with filtering
 * val transactions = client.getTransactions(
 *     TransactionsRequest(
 *         mcSeqno = 4488500u,
 *         account = listOf(myAddress),
 *         limit = 50u
 *     )
 * )
 * 
 * // Get jetton masters
 * val jettonMasters = client.getJettonMasters(
 *     JettonMastersRequest(limit = 20u)
 * )
 * ```
 * 
 * @param httpClient The HTTP client for making requests
 * @param basePath The base path for v3 API endpoints (defaults to "/toncenter/v3")
 */
class TonCenterV3Client(
    private val httpClient: TychoCenterHttpClient,
    private val basePath: String = "/toncenter/v3"
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    /**
     * Retrieves information about the current state of the masterchain.
     * 
     * @return Information about the first and last blocks in the masterchain
     */
    suspend fun getMasterchainInfo(): MasterchainInfoResponse {
        val response = httpClient.get("$basePath/masterchainInfo")
        return json.decodeFromString(response)
    }
    
    /**
     * Retrieves a list of blocks with optional filtering.
     * 
     * @param request Filtering parameters including workchain, shard, time ranges, and pagination
     * @return List of blocks matching the specified criteria
     */
    suspend fun getBlocks(request: BlocksRequest): BlocksResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/blocks", queryParams)
        return json.decodeFromString(response)
    }
    
    /**
     * Retrieves a list of transactions with comprehensive filtering options.
     * 
     * @param request Filtering parameters including accounts, time ranges, hashes, and pagination
     * @return List of transactions matching the specified criteria with address book
     */
    suspend fun getTransactions(request: TransactionsRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/transactions", queryParams)
        return json.decodeFromString(response)
    }
    
    /**
     * Retrieves transactions from a specific masterchain block.
     * 
     * @param request Parameters specifying the masterchain block sequence number and pagination
     * @return List of transactions from the specified masterchain block
     */
    suspend fun getTransactionsByMasterchainBlock(request: TransactionsByMcBlockRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/transactionsByMasterchainBlock", queryParams)
        return json.decodeFromString(response)
    }
    
    /**
     * Retrieves transactions adjacent to a specific transaction.
     * 
     * This method finds transactions that are logically connected to the specified
     * transaction, useful for tracing transaction chains and message flows.
     * 
     * @param request Parameters including the transaction hash and optional direction filter
     * @return List of adjacent transactions
     */
    suspend fun getAdjacentTransactions(request: AdjacentTransactionsRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/adjacentTransactions", queryParams)
        return json.decodeFromString(response)
    }
    
    /**
     * Retrieves transactions associated with a specific message.
     * 
     * @param request Parameters including message hash, body hash, opcode, and pagination
     * @return List of transactions associated with the specified message
     */
    suspend fun getTransactionsByMessage(request: TransactionsByMessageRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/transactionsByMessage", queryParams)
        return json.decodeFromString(response)
    }
    
    /**
     * Retrieves information about Jetton master contracts.
     * 
     * @param request Filtering parameters including addresses, admin addresses, and pagination
     * @return List of Jetton master contracts with their metadata
     */
    suspend fun getJettonMasters(request: JettonMastersRequest): JettonMastersResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/jetton/masters", queryParams)
        return json.decodeFromString(response)
    }
    
    /**
     * Retrieves information about Jetton wallet contracts.
     * 
     * @param request Filtering parameters including addresses, owner addresses, jetton addresses, and pagination
     * @return List of Jetton wallet contracts with their balances and metadata
     */
    suspend fun getJettonWallets(request: JettonWalletsRequest): JettonWalletsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/jetton/wallets", queryParams)
        return json.decodeFromString(response)
    }
    
    /**
     * Builds query parameters for blocks requests.
     * 
     * This method converts a BlocksRequest object into a map of query parameters
     * suitable for HTTP GET requests. It handles optional parameters by only
     * including them if they are not null.
     * 
     * @param request The blocks request with filtering parameters
     * @return Map of query parameter names to string values
     */
    private fun buildQueryParams(request: BlocksRequest): Map<String, String> {
        return buildMap {
            request.workchain?.let { put("workchain", it.toString()) }
            request.shard?.let { put("shard", it) }
            request.seqno?.let { put("seqno", it.toString()) }
            request.mcSeqno?.let { put("mc_seqno", it.toString()) }
            request.startUtime?.let { put("start_utime", it.toString()) }
            request.endUtime?.let { put("end_utime", it.toString()) }
            request.startLt?.let { put("start_lt", it.toString()) }
            request.endLt?.let { put("end_lt", it.toString()) }
            put("limit", request.limit.toString())
            put("offset", request.offset.toString())
            put("sort", request.sort.name.lowercase())
        }
    }
    
    /**
     * Builds query parameters for transactions requests.
     * 
     * This method handles the complex parameter mapping for transaction queries,
     * including address lists that need to be comma-separated and optional filters.
     * 
     * @param request The transactions request with filtering parameters
     * @return Map of query parameter names to string values
     */
    private fun buildQueryParams(request: TransactionsRequest): Map<String, String> {
        return buildMap {
            request.workchain?.let { put("workchain", it.toString()) }
            request.shard?.let { put("shard", it) }
            request.seqno?.let { put("seqno", it.toString()) }
            request.mcSeqno?.let { put("mc_seqno", it.toString()) }
            if (request.account.isNotEmpty()) {
                put("account", request.account.joinToString(",") { it.toString() })
            }
            if (request.excludeAccount.isNotEmpty()) {
                put("exclude_account", request.excludeAccount.joinToString(",") { it.toString() })
            }
            request.hash?.let { put("hash", it.value) }
            request.lt?.let { put("lt", it.toString()) }
            request.startUtime?.let { put("start_utime", it.toString()) }
            request.endUtime?.let { put("end_utime", it.toString()) }
            request.startLt?.let { put("start_lt", it.toString()) }
            request.endLt?.let { put("end_lt", it.toString()) }
            put("limit", request.limit.toString())
            put("offset", request.offset.toString())
            put("sort", request.sort.name.lowercase())
        }
    }
    
    /**
     * Builds query parameters for masterchain block transactions requests.
     * 
     * @param request The masterchain block request parameters
     * @return Map of query parameter names to string values
     */
    private fun buildQueryParams(request: TransactionsByMcBlockRequest): Map<String, String> {
        return buildMap {
            put("seqno", request.seqno.toString())
            put("limit", request.limit.toString())
            put("offset", request.offset.toString())
            put("sort", request.sort.name.lowercase())
        }
    }
    
    /**
     * Builds query parameters for adjacent transactions requests.
     * 
     * @param request The adjacent transactions request parameters
     * @return Map of query parameter names to string values
     */
    private fun buildQueryParams(request: AdjacentTransactionsRequest): Map<String, String> {
        return buildMap {
            put("hash", request.hash.value)
            request.direction?.let { put("direction", it.name.lowercase()) }
        }
    }
    
    /**
     * Builds query parameters for transactions by message requests.
     * 
     * @param request The message-based transactions request parameters
     * @return Map of query parameter names to string values
     */
    private fun buildQueryParams(request: TransactionsByMessageRequest): Map<String, String> {
        return buildMap {
            put("msg_hash", request.msgHash.value)
            request.bodyHash?.let { put("body_hash", it.value) }
            request.opcode?.let { put("opcode", it.toString()) }
            request.direction?.let { put("direction", it.name.lowercase()) }
            put("limit", request.limit.toString())
            put("offset", request.offset.toString())
            put("sort", request.sort.name.lowercase())
        }
    }
    
    /**
     * Builds query parameters for jetton masters requests.
     * 
     * Handles address list parameters by converting them to comma-separated strings.
     * 
     * @param request The jetton masters request parameters
     * @return Map of query parameter names to string values
     */
    private fun buildQueryParams(request: JettonMastersRequest): Map<String, String> {
        return buildMap {
            request.address?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    put("address", addresses.joinToString(",") { it.toString() })
                }
            }
            request.adminAddress?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    put("admin_address", addresses.joinToString(",") { it.toString() })
                }
            }
            put("limit", request.limit.toString())
            put("offset", request.offset.toString())
        }
    }
    
    /**
     * Builds query parameters for jetton wallets requests.
     * 
     * Handles multiple address list parameters and optional sorting.
     * 
     * @param request The jetton wallets request parameters
     * @return Map of query parameter names to string values
     */
    private fun buildQueryParams(request: JettonWalletsRequest): Map<String, String> {
        return buildMap {
            request.address?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    put("address", addresses.joinToString(",") { it.toString() })
                }
            }
            request.ownerAddress?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    put("owner_address", addresses.joinToString(",") { it.toString() })
                }
            }
            request.jettonAddress?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    put("jetton_address", addresses.joinToString(",") { it.toString() })
                }
            }
            put("exclude_zero_balance", request.excludeZeroBalance.toString())
            put("limit", request.limit.toString())
            put("offset", request.offset.toString())
            request.sort?.let { put("sort", it.name.lowercase()) }
        }
    }
}
