# Tycho TonCenter Kotlin

Kotlin bindings for the [Tycho TonCenter API](https://github.com/broxus/tycho-toncenter), providing access to both v2 (JSON-RPC) and v3 (REST) endpoints.

## Features

- **Complete API Coverage**: Supports both TonCenter API v2 and v3
- **Type Safety**: All request/response models as Kotlin data classes with proper serialization
- **Async Support**: Built with Kotlin coroutines for non-blocking operations
- **HTTP Client**: Uses OkHttp for reliable HTTP communication
- **JSON Serialization**: Uses Kotlinx Serialization for efficient JSON handling

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.broxus:tycho-toncenter-kotlin:0.1.0")
}
```

## Quick Start

```kotlin
import com.broxus.tycho.toncenter.TychoCenterClient
import com.broxus.tycho.toncenter.common.StdAddr
import com.broxus.tycho.toncenter.v2.AccountParams
import com.broxus.tycho.toncenter.v3.TransactionsRequest

suspend fun main() {
    val client = TychoCenterClient()
    
    try {
        // API v2 - Get masterchain info
        val masterchainInfo = client.v2.getMasterchainInfo()
        println("Last block seqno: ${masterchainInfo.last.seqno}")
        
        // API v2 - Get address information
        val address = StdAddr.parse("0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8")
        val addressInfo = client.v2.getAddressInformation(AccountParams(address))
        println("Balance: ${addressInfo.balance}")
        
        // API v3 - Get recent transactions
        val transactions = client.v3.getTransactions(
            TransactionsRequest(limit = 10u)
        )
        println("Found ${transactions.transactions.size} transactions")
        
        // API v3 - Get jetton masters
        val jettonMasters = client.v3.getJettonMasters(
            JettonMastersRequest(limit = 5u)
        )
        println("Found ${jettonMasters.jettonMasters.size} jetton masters")
        
    } finally {
        client.close()
    }
}
```

## API v2 (JSON-RPC)

The v2 API provides JSON-RPC methods for blockchain data access:

```kotlin
val client = TychoCenterClient()

// Get masterchain information
val masterchainInfo = client.v2.getMasterchainInfo()

// Get block header
val blockHeader = client.v2.getBlockHeader(
    BlockHeaderParams(
        workchain = -1,
        shard = -9223372036854775808L,
        seqno = 12345u
    )
)

// Get account transactions
val transactions = client.v2.getTransactions(
    TransactionsParams(
        address = StdAddr.parse("0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8"),
        limit = 10u
    )
)

// Run get method on smart contract
val result = client.v2.runGetMethod(
    RunGetMethodParams(
        address = StdAddr.parse("0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8"),
        method = "get_wallet_data",
        stack = emptyList()
    )
)
```

### Available v2 Methods

- `getMasterchainInfo()` - Get masterchain information
- `getBlockHeader()` - Get block header by coordinates
- `getShards()` - Get shard blocks for masterchain block
- `detectAddress()` - Detect address format and convert
- `getAddressInformation()` - Get account state
- `getExtendedAddressInformation()` - Get extended account state
- `getWalletInformation()` - Get wallet-specific information
- `getTokenData()` - Get jetton/NFT token data
- `getTransactions()` - Get account transactions
- `getBlockTransactions()` - Get block transactions
- `getBlockTransactionsExt()` - Get block transactions with full data
- `sendBoc()` - Send external message
- `sendBocReturnHash()` - Send external message and return hash
- `runGetMethod()` - Execute get method on smart contract

## API v3 (REST)

The v3 API provides REST endpoints with advanced filtering:

```kotlin
val client = TychoCenterClient()

// Get recent blocks
val blocks = client.v3.getBlocks(
    BlocksRequest(
        workchain = -1,
        limit = 10u,
        sort = SortDirection.DESC
    )
)

// Get transactions with filters
val transactions = client.v3.getTransactions(
    TransactionsRequest(
        account = listOf(StdAddr.parse("0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8")),
        startUtime = 1640995200u, // 2022-01-01
        limit = 50u
    )
)

// Get jetton wallets
val jettonWallets = client.v3.getJettonWallets(
    JettonWalletsRequest(
        ownerAddress = listOf(StdAddr.parse("0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8")),
        excludeZeroBalance = true
    )
)
```

### Available v3 Methods

- `getMasterchainInfo()` - Get first and last indexed blocks
- `getBlocks()` - Get blocks with filtering
- `getTransactions()` - Get transactions with advanced filtering
- `getTransactionsByMasterchainBlock()` - Get transactions by masterchain block
- `getAdjacentTransactions()` - Get parent/child transactions
- `getTransactionsByMessage()` - Get transactions by message hash
- `getJettonMasters()` - Get jetton master contracts
- `getJettonWallets()` - Get jetton wallet contracts

## Configuration

```kotlin
val client = TychoCenterClient(
    baseUrl = "https://toncenter-testnet.tychoprotocol.com",
    enableLogging = true,
    connectTimeoutMs = 30_000,
    readTimeoutMs = 60_000
)
```

## Error Handling

```kotlin
try {
    val result = client.v2.getMasterchainInfo()
} catch (e: HttpException) {
    println("HTTP error: ${e.code} - ${e.message}")
} catch (e: Exception) {
    println("Network error: ${e.message}")
}
```

## Data Types

The library provides type-safe data classes for all API models:

- `StdAddr` - TON/Tycho address representation
- `HashBytes` - Hash values (32 bytes)
- `Tokens` - TON/Tycho amounts with BigInteger precision
- `BlockId` - Block identification
- `Transaction` - Transaction data
- `Message` - Message data
- `JettonMaster` / `JettonWallet` - Jetton contract data

## Thread Safety

The client is thread-safe and can be used from multiple coroutines simultaneously. However, you should call `client.close()` when done to properly clean up resources.

## License

Apache License, Version 2.0 (LICENSE-APACHE or https://www.apache.org/licenses/LICENSE-2.0)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
