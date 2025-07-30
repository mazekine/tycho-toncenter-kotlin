package com.broxus.tycho.toncenter.v3

import com.broxus.tycho.toncenter.common.TychoCenterHttpClient
import kotlinx.serialization.json.Json

class TonCenterV3Client(
    private val httpClient: TychoCenterHttpClient,
    private val basePath: String = "/toncenter/v3"
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    suspend fun getMasterchainInfo(): MasterchainInfoResponse {
        val response = httpClient.get("$basePath/masterchainInfo")
        return json.decodeFromString(response)
    }
    
    suspend fun getBlocks(request: BlocksRequest): BlocksResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/blocks", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getTransactions(request: TransactionsRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/transactions", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getTransactionsByMasterchainBlock(request: TransactionsByMcBlockRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/transactionsByMasterchainBlock", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getAdjacentTransactions(request: AdjacentTransactionsRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/adjacentTransactions", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getTransactionsByMessage(request: TransactionsByMessageRequest): TransactionsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/transactionsByMessage", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getJettonMasters(request: JettonMastersRequest): JettonMastersResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/jetton/masters", queryParams)
        return json.decodeFromString(response)
    }
    
    suspend fun getJettonWallets(request: JettonWalletsRequest): JettonWalletsResponse {
        val queryParams = buildQueryParams(request)
        val response = httpClient.get("$basePath/jetton/wallets", queryParams)
        return json.decodeFromString(response)
    }
    
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
    
    private fun buildQueryParams(request: TransactionsByMcBlockRequest): Map<String, String> {
        return buildMap {
            put("seqno", request.seqno.toString())
            put("limit", request.limit.toString())
            put("offset", request.offset.toString())
            put("sort", request.sort.name.lowercase())
        }
    }
    
    private fun buildQueryParams(request: AdjacentTransactionsRequest): Map<String, String> {
        return buildMap {
            put("hash", request.hash.value)
            request.direction?.let { put("direction", it.name.lowercase()) }
        }
    }
    
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
