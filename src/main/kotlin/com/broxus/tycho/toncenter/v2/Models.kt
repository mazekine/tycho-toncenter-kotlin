package com.broxus.tycho.toncenter.v2

import com.broxus.tycho.toncenter.common.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigInteger

/**
 * Empty parameters for API methods that don't require input.
 * 
 * @param dummy Placeholder field to ensure valid JSON serialization
 */
@Serializable
data class EmptyParams(val dummy: String = "")

/**
 * Parameters for retrieving shard information.
 * 
 * @param seqno The masterchain sequence number
 */
@Serializable
data class GetShardsParams(val seqno: UInt)

/**
 * Parameters for address detection and conversion.
 * 
 * @param address The address string to detect and convert
 */
@Serializable
data class DetectAddressParams(val address: String)

/**
 * Parameters for account-related queries.
 * 
 * @param address The account address to query
 */
@Serializable
data class AccountParams(val address: StdAddr)

/**
 * Parameters for retrieving block header information.
 * 
 * @param workchain The workchain ID
 * @param shard The shard identifier
 * @param seqno The block sequence number
 * @param rootHash Optional root hash for additional verification
 * @param fileHash Optional file hash for additional verification
 */
@Serializable
data class BlockHeaderParams(
    val workchain: Int,
    val shard: Long,
    val seqno: UInt,
    val rootHash: HashBytes? = null,
    val fileHash: HashBytes? = null
)

/**
 * Parameters for retrieving account transactions with pagination.
 * 
 * @param address The account address to query
 * @param limit Maximum number of transactions to return (1-100)
 * @param lt Optional logical time for pagination (start from this LT)
 * @param hash Optional transaction hash for pagination
 * @param toLt Logical time to stop at (0 means no limit)
 */
@Serializable
data class TransactionsParams(
    val address: StdAddr,
    val limit: UByte = 10u,
    val lt: ULong? = null,
    val hash: HashBytes? = null,
    val toLt: ULong = 0u
)

/**
 * Parameters for retrieving transactions from a specific block.
 * 
 * @param workchain The workchain ID
 * @param shard The shard identifier
 * @param seqno The block sequence number
 * @param rootHash Optional root hash for additional verification
 * @param fileHash Optional file hash for additional verification
 * @param afterLt Optional logical time for pagination
 * @param afterHash Optional transaction hash for pagination
 * @param count Maximum number of transactions to return
 */
@Serializable
data class BlockTransactionsParams(
    val workchain: Int,
    val shard: Long,
    val seqno: UInt,
    val rootHash: HashBytes? = null,
    val fileHash: HashBytes? = null,
    val afterLt: ULong? = null,
    val afterHash: HashBytes? = null,
    val count: UByte = 10u
)

/**
 * Parameters for sending a serialized message to the blockchain.
 * 
 * @param boc The serialized message as a base64-encoded BOC (Bag of Cells)
 */
@Serializable
data class SendBocParams(val boc: String)

/**
 * Parameters for executing a get method on a smart contract.
 * 
 * @param address The smart contract address
 * @param method The method name to execute
 * @param stack The input stack for the method call
 */
@Serializable
data class RunGetMethodParams(
    val address: StdAddr,
    val method: String,
    val stack: List<TonlibInputStackItem>
)

/**
 * Sealed class representing different types of input stack items for smart contract method calls.
 * 
 * The TON VM uses a stack-based execution model, and this sealed class represents
 * the different types of values that can be passed as arguments to smart contract methods.
 */
@Serializable
sealed class TonlibInputStackItem {
    /**
     * Numeric value on the stack.
     * 
     * @param value The numeric value as a string (can represent very large integers)
     */
    @Serializable
    @SerialName("num")
    data class Num(val value: String) : TonlibInputStackItem()
    
    /**
     * Cell value on the stack.
     * 
     * @param bytes The cell data as a base64-encoded string
     */
    @Serializable
    @SerialName("cell")
    data class Cell(val bytes: String) : TonlibInputStackItem()
    
    /**
     * Slice value on the stack.
     * 
     * @param bytes The slice data as a base64-encoded string
     */
    @Serializable
    @SerialName("slice")
    data class Slice(val bytes: String) : TonlibInputStackItem()
}

/**
 * Response wrapper for masterchain info in v2 API.
 * 
 * The v2 API wraps all responses in a standard format with `ok` and `result` fields.
 * 
 * @param result The actual masterchain information
 * @param ok Whether the request was successful
 */
@Serializable
data class MasterchainInfoResponseWrapper(
    val result: MasterchainInfoResponse,
    val ok: Boolean
)

/**
 * Masterchain information response from the v2 API.
 * 
 * Contains information about the current state of the masterchain including
 * the last processed block and initialization block.
 * 
 * @param type The response type identifier (always "blocks.masterchainInfo")
 * @param last The last processed block
 * @param stateRootHash The state root hash
 * @param init The initialization block
 * @param extra Additional metadata
 */
@Serializable
data class MasterchainInfoResponse(
    @SerialName("@type") val type: String,
    val last: TonlibBlockId,
    @SerialName("state_root_hash") val stateRootHash: String,
    val init: TonlibBlockId,
    @SerialName("@extra") val extra: String = ""
)

/**
 * Block identifier used in the v2 API.
 * 
 * Contains all necessary information to uniquely identify a block in the TON blockchain.
 * 
 * @param type The block ID type (always "ton.blockIdExt")
 * @param workchain The workchain ID
 * @param shard The shard identifier as a string
 * @param seqno The block sequence number
 * @param rootHash The block root hash
 * @param fileHash The block file hash
 */
@Serializable
data class TonlibBlockId(
    @SerialName("@type") val type: String = "ton.blockIdExt",
    val workchain: Int,
    val shard: String,
    val seqno: UInt,
    @SerialName("root_hash") val rootHash: String,
    @SerialName("file_hash") val fileHash: String
)

/**
 * Response containing shard information for a masterchain block.
 * 
 * @param type The response type identifier
 * @param shards List of shard blocks for the specified masterchain block
 * @param extra Additional metadata
 */
@Serializable
data class ShardsResponse(
    @SerialName("@type") val type: String,
    val shards: List<TonlibBlockId>,
    @SerialName("@extra") val extra: String = ""
)

/**
 * Response containing different representations of a TON address.
 * 
 * @param rawForm The raw workchain:address format
 * @param bounceable Base64 encoded bounceable address forms
 * @param nonBounceable Base64 encoded non-bounceable address forms
 * @param givenType The type of the input address
 * @param testOnly Whether this is a testnet address
 */
@Serializable
data class AddressFormsResponse(
    val rawForm: StdAddr,
    val bounceable: Base64Form,
    val nonBounceable: Base64Form,
    val givenType: AddressType,
    val testOnly: Boolean
)

/**
 * Base64 encoded address forms.
 * 
 * @param b64 Standard base64 encoding
 * @param b64url URL-safe base64 encoding
 */
@Serializable
data class Base64Form(
    val b64: String,
    val b64url: String
)

/**
 * Enumeration of TON address types.
 */
@Serializable
enum class AddressType {
    @SerialName("raw_form") RAW_FORM,
    @SerialName("friendly_bounceable") FRIENDLY_BOUNCEABLE,
    @SerialName("friendly_non_bounceable") FRIENDLY_NON_BOUNCEABLE
}

/**
 * Response wrapper for address information in v2 API.
 * 
 * @param result The actual address information
 * @param ok Whether the request was successful
 */
@Serializable
data class AddressInformationResponseWrapper(
    val result: AddressInformationResponse,
    val ok: Boolean
)

/**
 * Detailed information about an account/address.
 * 
 * @param type The response type identifier
 * @param balance The account balance in nanotons as a string
 * @param extraCurrencies List of extra currencies (if any)
 * @param code The smart contract code (if any)
 * @param data The smart contract data (if any)
 * @param lastTransactionId The last transaction ID for this account
 * @param blockId The block where this information was retrieved
 * @param frozenHash Hash for frozen accounts (if applicable)
 * @param syncUtime The synchronization timestamp
 * @param extra Additional metadata
 * @param state The account state (active, uninitialized, frozen)
 */
@Serializable
data class AddressInformationResponse(
    @SerialName("@type") val type: String,
    val balance: String,
    @SerialName("extra_currencies") val extraCurrencies: List<String> = emptyList(),
    val code: String? = null,
    val data: String? = null,
    @SerialName("last_transaction_id") val lastTransactionId: TonlibTransactionId,
    @SerialName("block_id") val blockId: TonlibBlockId,
    @SerialName("frozen_hash") val frozenHash: String? = null,
    @SerialName("sync_utime") val syncUtime: UInt,
    @SerialName("@extra") val extra: String = "",
    val state: String
)

/**
 * Enumeration of account states in the TON blockchain.
 */
@Serializable
enum class TonlibAccountStatus {
    @SerialName("uninitialized") UNINITIALIZED,
    @SerialName("frozen") FROZEN,
    @SerialName("active") ACTIVE
}

/**
 * Transaction identifier consisting of logical time and hash.
 * 
 * @param lt The logical time of the transaction
 * @param hash The transaction hash
 */
@Serializable
data class TonlibTransactionId(
    val lt: ULong,
    val hash: HashBytes
) {
    companion object {
        /**
         * Creates a default transaction ID with zero values.
         */
        fun default() = TonlibTransactionId(0u, HashBytes.ZERO)
    }
}

/**
 * Extended address information response with detailed account state parsing.
 * 
 * This response provides comprehensive information about an account including
 * parsed account state that can identify specific wallet types and their parameters.
 * 
 * @param type The response type identifier
 * @param address The account address wrapper
 * @param balance The account balance
 * @param extraCurrencies List of extra currencies (if any)
 * @param lastTransactionId The last transaction ID for this account
 * @param blockId The block where this information was retrieved
 * @param syncUtime The synchronization timestamp
 * @param accountState The parsed account state with type-specific information
 * @param revision The state revision number
 * @param extra Additional metadata
 */
@Serializable
data class ExtendedAddressInformationResponse(
    @SerialName("@type") val type: String,
    val address: TonlibAddress,
    val balance: Tokens,
    val extraCurrencies: List<String> = emptyList(),
    val lastTransactionId: TonlibTransactionId,
    val blockId: TonlibBlockId,
    val syncUtime: UInt,
    val accountState: ParsedAccountState,
    val revision: Int,
    @SerialName("@extra") val extra: String = ""
)

/**
 * Wrapper for TON blockchain addresses in the v2 API format.
 * 
 * @param type The address type identifier
 * @param accountAddress The actual account address
 */
@Serializable
data class TonlibAddress(
    @SerialName("@type") val type: String = "accountAddress",
    val accountAddress: StdAddr
)

/**
 * Sealed class representing different parsed account states.
 * 
 * The v2 API can parse account states to identify specific wallet types
 * and extract relevant parameters like sequence numbers and public keys.
 */
@Serializable
sealed class ParsedAccountState {
    /**
     * Uninitialized account state.
     * 
     * @param frozenHash Hash for frozen accounts (if applicable)
     */
    @Serializable
    @SerialName("uninit")
    data class Uninit(val frozenHash: HashBytes? = null) : ParsedAccountState()
    
    /**
     * Raw account state with code and data.
     * 
     * @param code The smart contract code (if any)
     * @param data The smart contract data (if any)
     * @param frozenHash Hash for frozen accounts (if applicable)
     */
    @Serializable
    @SerialName("raw")
    data class Raw(
        val code: String? = null,
        val data: String? = null,
        val frozenHash: HashBytes? = null
    ) : ParsedAccountState()
    
    /**
     * Wallet v3 account state with extracted parameters.
     * 
     * @param seqno The wallet sequence number
     * @param publicKey The wallet public key
     * @param walletId The wallet identifier
     */
    @Serializable
    @SerialName("wallet_v3")
    data class WalletV3(
        val seqno: UInt,
        val publicKey: String,
        val walletId: UInt
    ) : ParsedAccountState()
    
    /**
     * Wallet v4 account state with extracted parameters.
     * 
     * @param seqno The wallet sequence number
     * @param publicKey The wallet public key
     * @param walletId The wallet identifier
     */
    @Serializable
    @SerialName("wallet_v4")
    data class WalletV4(
        val seqno: UInt,
        val publicKey: String,
        val walletId: UInt
    ) : ParsedAccountState()
}

/**
 * Wallet-specific information response from the v2 API.
 * 
 * @param wallet Whether the address is a wallet contract
 * @param balance The wallet balance
 * @param extraCurrencies List of extra currencies (if any)
 * @param accountState The current account state
 * @param walletType The detected wallet type (if applicable)
 * @param seqno The wallet sequence number (if applicable)
 * @param lastTransactionId The last transaction ID for this wallet
 */
@Serializable
data class WalletInformationResponse(
    val wallet: Boolean,
    val balance: Tokens,
    val extraCurrencies: List<String> = emptyList(),
    val accountState: TonlibAccountStatus,
    val walletType: String? = null,
    val seqno: UInt? = null,
    val lastTransactionId: TonlibTransactionId
)

/**
 * Sealed class representing different types of token data in the v2 API.
 * 
 * The v2 API supports querying information about Jetton master contracts
 * and individual Jetton wallet contracts.
 */
@Serializable
sealed class TokenData {
    /**
     * Jetton master contract data.
     * 
     * @param totalSupply The total supply of the Jetton
     * @param mintable Whether new tokens can be minted
     * @param adminAddress The admin address (if any)
     * @param jettonContent The Jetton metadata content
     * @param jettonWalletCode The Jetton wallet contract code
     */
    @Serializable
    @SerialName("jetton_master")
    data class JettonMaster(
        @Contextual val totalSupply: BigInteger,
        val mintable: Boolean,
        val adminAddress: StdAddr? = null,
        val jettonContent: JettonContent,
        val jettonWalletCode: String
    ) : TokenData()
    
    /**
     * Jetton wallet contract data.
     * 
     * @param balance The wallet's Jetton balance
     * @param owner The wallet owner's address
     * @param jetton The Jetton master contract address
     * @param jettonWalletCode The wallet contract code
     */
    @Serializable
    @SerialName("jetton_wallet")
    data class JettonWallet(
        @Contextual val balance: BigInteger,
        val owner: StdAddr,
        val jetton: StdAddr,
        val jettonWalletCode: String
    ) : TokenData()
}

/**
 * Sealed class representing different types of Jetton content storage.
 * 
 * Jetton metadata can be stored either on-chain or off-chain, with different
 * data structures for each approach.
 */
@Serializable
sealed class JettonContent {
    /**
     * On-chain Jetton content stored directly in the contract.
     * 
     * @param data Key-value map of metadata fields
     */
    @Serializable
    @SerialName("onchain")
    data class Onchain(val data: Map<String, String>) : JettonContent()
    
    /**
     * Off-chain Jetton content referenced by URI.
     * 
     * @param data URI or reference to external metadata
     */
    @Serializable
    @SerialName("offchain")
    data class Offchain(val data: String) : JettonContent()
}

/**
 * Block header information response from the v2 API.
 * 
 * Contains comprehensive metadata about a block header including validation
 * information, timing data, and references to related blocks.
 * 
 * @param type The response type identifier
 * @param id The block identifier
 * @param globalId The global blockchain ID
 * @param version The block format version
 * @param flags Block flags
 * @param afterMerge Whether this block was created after a shard merge
 * @param afterSplit Whether this block was created after a shard split
 * @param beforeSplit Whether this block was created before a shard split
 * @param wantMerge Whether this block wants to merge with another shard
 * @param wantSplit Whether this block wants to split the shard
 * @param validatorListHashShort Short hash of the validator list
 * @param catchainSeqno Catchain sequence number for block generation
 * @param minRefMcSeqno Minimum referenced masterchain sequence number
 * @param isKeyBlock Whether this is a key block (contains validator set changes)
 * @param prevKeyBlockSeqno Previous key block sequence number
 * @param startLt Starting logical time for this block
 * @param endLt Ending logical time for this block
 * @param genUtime Block generation time (Unix timestamp)
 * @param vertSeqno Vertical sequence number
 * @param prevBlocks References to previous blocks
 * @param extra Additional metadata
 */
@Serializable
data class BlockHeaderResponse(
    @SerialName("@type") val type: String,
    val id: TonlibBlockId,
    val globalId: Int,
    val version: UInt,
    val flags: UByte,
    val afterMerge: Boolean,
    val afterSplit: Boolean,
    val beforeSplit: Boolean,
    val wantMerge: Boolean,
    val wantSplit: Boolean,
    val validatorListHashShort: UInt,
    val catchainSeqno: UInt,
    val minRefMcSeqno: UInt,
    val isKeyBlock: Boolean,
    val prevKeyBlockSeqno: UInt,
    val startLt: ULong,
    val endLt: ULong,
    val genUtime: UInt,
    val vertSeqno: UInt,
    val prevBlocks: List<TonlibBlockId>,
    @SerialName("@extra") val extra: String = ""
)

/**
 * Transaction information from the v2 API.
 * 
 * Represents a complete transaction with all associated messages and fee information.
 * 
 * @param type The transaction type identifier
 * @param address The account address involved in the transaction
 * @param utime The transaction timestamp (Unix time)
 * @param data The raw transaction data
 * @param transactionId The transaction identifier
 * @param fee The total transaction fee
 * @param storageFee The storage fee component
 * @param otherFee Other fee components
 * @param inMsg The incoming message (if any)
 * @param outMsgs List of outgoing messages
 */
@Serializable
data class TonlibTransaction(
    @SerialName("@type") val type: String = "raw.transaction",
    val address: TonlibAddress,
    val utime: UInt,
    val data: String,
    val transactionId: TonlibTransactionId,
    val fee: Tokens,
    val storageFee: Tokens,
    val otherFee: Tokens,
    val inMsg: TonlibMessage? = null,
    val outMsgs: List<TonlibMessage>
)

/**
 * Message information from the v2 API.
 * 
 * Represents a message between accounts with value transfer and fee information.
 * 
 * @param type The message type identifier
 * @param hash The message hash
 * @param source The source address (if any)
 * @param destination The destination address (if any)
 * @param value The value being transferred
 * @param extraCurrencies List of extra currencies (if any)
 * @param fwdFee The forward fee
 * @param ihrFee The IHR (Instant Hypercube Routing) fee
 * @param createdLt The logical time when the message was created
 * @param bodyHash The message body hash
 * @param msgData The message data
 */
@Serializable
data class TonlibMessage(
    @SerialName("@type") val type: String = "raw.message",
    val hash: HashBytes,
    val source: StdAddr? = null,
    val destination: StdAddr? = null,
    val value: Tokens,
    val extraCurrencies: List<String> = emptyList(),
    val fwdFee: Tokens,
    val ihrFee: Tokens,
    val createdLt: ULong,
    val bodyHash: HashBytes,
    val msgData: TonlibMessageData
)

/**
 * Message data containing the actual message content.
 * 
 * @param type The message data type identifier
 * @param body The message body as a string
 * @param initState The initial state data (if any)
 */
@Serializable
data class TonlibMessageData(
    @SerialName("@type") val type: String = "msg.dataRaw",
    val body: String,
    val initState: String? = null
)

@Serializable
data class ExtMsgInfoResponse(
    @SerialName("@type") val type: String,
    val hash: HashBytes,
    val hashNorm: HashBytes,
    @SerialName("@extra") val extra: String = ""
)

@Serializable
data class RunGetMethodResponse(
    @SerialName("@type") val type: String,
    val exitCode: Int,
    val gasUsed: ULong,
    val stack: List<TonlibOutputStackItem>,
    val lastTransactionId: TonlibTransactionId,
    val blockId: TonlibBlockId,
    @SerialName("@extra") val extra: String = ""
)

@Serializable
sealed class TonlibOutputStackItem {
    @Serializable
    @SerialName("num")
    data class Num(val value: String) : TonlibOutputStackItem()
    
    @Serializable
    @SerialName("cell")
    data class Cell(val bytes: String) : TonlibOutputStackItem()
    
    @Serializable
    @SerialName("slice")
    data class Slice(val bytes: String) : TonlibOutputStackItem()
    
    @Serializable
    @SerialName("list")
    data class List(val elements: kotlin.collections.List<TonlibOutputStackItem>) : TonlibOutputStackItem()
    
    @Serializable
    @SerialName("tuple")
    data class Tuple(val elements: kotlin.collections.List<TonlibOutputStackItem>) : TonlibOutputStackItem()
}

@Serializable
data class GetBlockTransactionsResponse(
    @SerialName("@type") val type: String,
    val id: TonlibBlockId,
    val reqCount: UByte,
    val transactions: List<TonlibBlockTransactionId>,
    val incomplete: Boolean,
    @SerialName("@extra") val extra: String = ""
)

@Serializable
data class TonlibBlockTransactionId(
    @SerialName("@type") val type: String,
    val mode: UByte,
    val account: StdAddr,
    val lt: ULong,
    val hash: HashBytes
)

@Serializable
data class GetBlockTransactionsExtResponse(
    @SerialName("@type") val type: String,
    val id: TonlibBlockId,
    val reqCount: UByte,
    val transactions: List<TonlibBlockTransaction>,
    val incomplete: Boolean,
    @SerialName("@extra") val extra: String = ""
)

@Serializable
data class TonlibBlockTransaction(
    val tx: TonlibTransaction,
    val account: StdAddr
)
