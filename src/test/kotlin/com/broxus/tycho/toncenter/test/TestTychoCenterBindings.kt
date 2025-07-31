package com.broxus.tycho.toncenter.test

import com.broxus.tycho.toncenter.TychoCenterClient
import com.broxus.tycho.toncenter.v2.AccountParams
import com.broxus.tycho.toncenter.v3.TransactionsRequest
import com.broxus.tycho.toncenter.common.StdAddr
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val baseUrl = System.getenv("TYCHO_TONCENTER_URL") ?: "https://toncenter-testnet.tychoprotocol.com"
    println("Testing Kotlin bindings against: $baseUrl")
    
    val client = TychoCenterClient(
        baseUrl = baseUrl,
        enableLogging = true
    )
    
    try {
        println("\n=== Testing API v2 ===")
        
        // Test v2 getMasterchainInfo
        println("1. Testing v2 getMasterchainInfo()...")
        try {
            val masterchainInfo = client.v2.getMasterchainInfo()
            println("✅ Success! Last block seqno: ${masterchainInfo.last.seqno}")
            println("   Block workchain: ${masterchainInfo.last.workchain}")
            println("   Block shard: ${masterchainInfo.last.shard}")
        } catch (e: Exception) {
            println("❌ Failed: ${e.message}")
        }
        
        // Test v2 getAddressInformation with a known address
        println("\n2. Testing v2 getAddressInformation()...")
        try {
            val testAddress = StdAddr.parse("0:0000000000000000000000000000000000000000000000000000000000000000")
            val addressInfo = client.v2.getAddressInformation(AccountParams(testAddress))
            println("✅ Success! Address state: ${addressInfo.state}")
            println("   Balance: ${addressInfo.balance}")
        } catch (e: Exception) {
            println("❌ Failed: ${e.message}")
        }
        
        println("\n=== Testing API v3 ===")
        
        // Test v3 getMasterchainInfo
        println("3. Testing v3 getMasterchainInfo()...")
        try {
            val masterchainInfo = client.v3.getMasterchainInfo()
            println("✅ Success! Last block seqno: ${masterchainInfo.last.seqno}")
            println("   First block seqno: ${masterchainInfo.first.seqno}")
        } catch (e: Exception) {
            println("❌ Failed: ${e.message}")
        }
        
        // Test v3 getTransactions
        println("\n4. Testing v3 getTransactions()...")
        try {
            val transactions = client.v3.getTransactions(
                TransactionsRequest(limit = 5u)
            )
            println("✅ Success! Found ${transactions.transactions.size} transactions")
            if (transactions.transactions.isNotEmpty()) {
                val firstTx = transactions.transactions.first()
                println("   First transaction hash: ${firstTx.hash}")
                println("   Account: ${firstTx.account}")
            }
        } catch (e: Exception) {
            println("❌ Failed: ${e.message}")
        }
        
        // Test v3 getBlocks
        println("\n5. Testing v3 getBlocks()...")
        try {
            val blocks = client.v3.getBlocks(
                com.broxus.tycho.toncenter.v3.BlocksRequest(limit = 3u)
            )
            println("✅ Success! Found ${blocks.blocks.size} blocks")
            if (blocks.blocks.isNotEmpty()) {
                val firstBlock = blocks.blocks.first()
                println("   First block seqno: ${firstBlock.seqno}")
                println("   Workchain: ${firstBlock.workchain}")
            }
        } catch (e: Exception) {
            println("❌ Failed: ${e.message}")
        }
        
        println("\n=== Test Summary ===")
        println("All tests completed. Check results above for any failures.")
        
    } finally {
        client.close()
        println("\nClient closed successfully.")
    }
}
