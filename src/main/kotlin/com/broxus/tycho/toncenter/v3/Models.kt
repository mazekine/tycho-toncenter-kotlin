package com.broxus.tycho.toncenter.v3

import com.broxus.tycho.toncenter.common.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.math.BigInteger

/**
 * Request parameters for retrieving blocks with comprehensive filtering options.
 * 
 * The v3 API provides advanced filtering capabilities allowing queries by workchain,
 * shard, sequence numbers, time ranges, and logical time ranges with pagination support.
 * 
 * @param workchain Optional workchain filter (-1 for masterchain, 0 for basechain)
 * @param shard Optional shard identifier filter
 * @param seqno Optional specific block sequence number
 * @param mcSeqno Optional masterchain sequence number filter
 * @param startUtime Optional start time filter (Unix timestamp)
 * @param endUtime Optional end time filter (Unix timestamp)
 * @param startLt Optional start logical time filter
 * @param endLt Optional end logical time filter
 * @param limit Maximum number of results to return (default: 10)
 * @param offset Number of results to skip for pagination (default: 0)
 * @param sort Sort direction for results (default: DESC)
 */
@Serializable
data class BlocksRequest(
    val workchain: Int? = null,
    val shard: String? = null,
    val seqno: UInt? = null,
    val mcSeqno: UInt? = null,
    val startUtime: UInt? = null,
    val endUtime: UInt? = null,
    val startLt: ULong? = null,
    val endLt: ULong? = null,
    val limit: UInt = 10u,
    val offset: UInt = 0u,
    val sort: SortDirection = SortDirection.DESC
)

/**
 * Request parameters for retrieving transactions with extensive filtering capabilities.
 * 
 * This request type supports complex queries including multiple account filters,
 * exclusion lists, hash-based lookups, time ranges, and logical time ranges.
 * 
 * @param workchain Optional workchain filter
 * @param shard Optional shard identifier filter
 * @param seqno Optional block sequence number filter
 * @param mcSeqno Optional masterchain sequence number filter
 * @param account List of account addresses to include (empty means no filter)
 * @param excludeAccount List of account addresses to exclude
 * @param hash Optional specific transaction hash
 * @param lt Optional specific logical time
 * @param startUtime Optional start time filter (Unix timestamp)
 * @param endUtime Optional end time filter (Unix timestamp)
 * @param startLt Optional start logical time filter
 * @param endLt Optional end logical time filter
 * @param limit Maximum number of results to return (default: 10)
 * @param offset Number of results to skip for pagination (default: 0)
 * @param sort Sort direction for results (default: DESC)
 */
@Serializable
data class TransactionsRequest(
    val workchain: Int? = null,
    val shard: String? = null,
    val seqno: UInt? = null,
    val mcSeqno: UInt? = null,
    val account: List<StdAddr> = emptyList(),
    val excludeAccount: List<StdAddr> = emptyList(),
    val hash: HashBytes? = null,
    val lt: ULong? = null,
    val startUtime: UInt? = null,
    val endUtime: UInt? = null,
    val startLt: ULong? = null,
    val endLt: ULong? = null,
    val limit: UInt = 10u,
    val offset: UInt = 0u,
    val sort: SortDirection = SortDirection.DESC
)

/**
 * Request parameters for retrieving transactions from a specific masterchain block.
 * 
 * @param seqno The masterchain block sequence number
 * @param limit Maximum number of results to return (default: 10)
 * @param offset Number of results to skip for pagination (default: 0)
 * @param sort Sort direction for results (default: DESC)
 */
@Serializable
data class TransactionsByMcBlockRequest(
    val seqno: UInt,
    val limit: UInt = 10u,
    val offset: UInt = 0u,
    val sort: SortDirection = SortDirection.DESC
)

/**
 * Request parameters for finding transactions adjacent to a specific transaction.
 * 
 * Adjacent transactions are those that are logically connected through message flows.
 * 
 * @param hash The transaction hash to find adjacent transactions for
 * @param direction Optional direction filter (incoming or outgoing messages)
 */
@Serializable
data class AdjacentTransactionsRequest(
    val hash: HashBytes,
    val direction: MessageDirection? = null
)

/**
 * Request parameters for finding transactions associated with a specific message.
 * 
 * @param msgHash The message hash to search for
 * @param bodyHash Optional message body hash for more specific filtering
 * @param opcode Optional operation code filter
 * @param direction Optional message direction filter
 * @param limit Maximum number of results to return (default: 10)
 * @param offset Number of results to skip for pagination (default: 0)
 * @param sort Sort direction for results (default: DESC)
 */
@Serializable
data class TransactionsByMessageRequest(
    val msgHash: HashBytes,
    val bodyHash: HashBytes? = null,
    val opcode: Int? = null,
    val direction: MessageDirection? = null,
    val limit: UInt = 10u,
    val offset: UInt = 0u,
    val sort: SortDirection = SortDirection.DESC
)

/**
 * Request parameters for retrieving Jetton master contract information.
 * 
 * @param address Optional list of specific Jetton master addresses to query
 * @param adminAddress Optional list of admin addresses to filter by
 * @param limit Maximum number of results to return (default: 10)
 * @param offset Number of results to skip for pagination (default: 0)
 */
@Serializable
data class JettonMastersRequest(
    val address: List<StdAddr>? = null,
    val adminAddress: List<StdAddr>? = null,
    val limit: UInt = 10u,
    val offset: UInt = 0u
)

/**
 * Request parameters for retrieving Jetton wallet contract information.
 * 
 * @param address Optional list of specific Jetton wallet addresses to query
 * @param ownerAddress Optional list of wallet owner addresses to filter by
 * @param jettonAddress Optional list of Jetton master addresses to filter by
 * @param excludeZeroBalance Whether to exclude wallets with zero balance (default: false)
 * @param limit Maximum number of results to return (default: 10)
 * @param offset Number of results to skip for pagination (default: 0)
 * @param sort Optional sort direction for results
 */
@Serializable
data class JettonWalletsRequest(
    val address: List<StdAddr>? = null,
    val ownerAddress: List<StdAddr>? = null,
    val jettonAddress: List<StdAddr>? = null,
    val excludeZeroBalance: Boolean = false,
    val limit: UInt = 10u,
    val offset: UInt = 0u,
    val sort: SortDirection? = null
)

/**
 * Enumeration for sort direction in API requests.
 */
@Serializable
enum class SortDirection {
    @SerialName("asc") ASC,
    @SerialName("desc") DESC
}

/**
 * Enumeration for message direction filtering.
 */
@Serializable
enum class MessageDirection {
    @SerialName("in") IN,
    @SerialName("out") OUT
}

/**
 * Response containing masterchain information from the v3 API.
 * 
 * @param last Information about the last processed block
 * @param first Information about the first known block
 */
@Serializable
data class MasterchainInfoResponse(
    val last: Block,
    val first: Block
)

/**
 * Response containing a list of blocks from the v3 API.
 * 
 * @param blocks List of block information matching the query criteria
 */
@Serializable
data class BlocksResponse(
    val blocks: List<Block>
)

/**
 * Response containing transactions and associated address information.
 * 
 * The v3 API includes an address book that provides additional context
 * about addresses referenced in the transactions.
 * 
 * @param transactions List of transactions matching the query criteria
 * @param addressBook Address book containing additional address information
 */
@Serializable
data class TransactionsResponse(
    val transactions: List<Transaction>,
    @SerialName("address_book") val addressBook: AddressBook
)

/**
 * Response containing Jetton master contract information.
 * 
 * @param jettonMasters List of Jetton master contracts
 * @param addressBook Address book with additional address context
 */
@Serializable
data class JettonMastersResponse(
    val jettonMasters: List<JettonMastersResponseItem>,
    val addressBook: AddressBook
)

/**
 * Information about a Jetton master contract.
 * 
 * @param address The Jetton master contract address
 * @param totalSupply The total supply of the Jetton
 * @param mintable Whether new tokens can be minted
 * @param adminAddress The admin address (if any)
 * @param jettonContent The Jetton metadata content (can be on-chain or off-chain)
 * @param jettonWalletCodeHash Hash of the Jetton wallet code
 * @param codeHash Hash of the master contract code
 * @param dataHash Hash of the master contract data
 * @param lastTransactionLt Logical time of the last transaction
 */
@Serializable
data class JettonMastersResponseItem(
    val address: StdAddr,
    @Contextual val totalSupply: BigInteger,
    val mintable: Boolean,
    val adminAddress: StdAddr? = null,
    val jettonContent: JsonElement? = null,
    val jettonWalletCodeHash: HashBytes,
    val codeHash: HashBytes,
    val dataHash: HashBytes,
    val lastTransactionLt: ULong
)

/**
 * Response containing Jetton wallet contract information.
 * 
 * @param jettonWallets List of Jetton wallet contracts
 * @param addressBook Address book with additional address context
 */
@Serializable
data class JettonWalletsResponse(
    val jettonWallets: List<JettonWalletsResponseItem>,
    val addressBook: AddressBook
)

/**
 * Information about a Jetton wallet contract.
 * 
 * @param address The Jetton wallet contract address
 * @param balance The wallet's Jetton balance
 * @param owner The wallet owner's address
 * @param jetton The Jetton master contract address
 * @param lastTransactionLt Logical time of the last transaction
 * @param codeHash Hash of the wallet contract code (if available)
 * @param dataHash Hash of the wallet contract data (if available)
 */
@Serializable
data class JettonWalletsResponseItem(
    val address: StdAddr,
    @Contextual val balance: BigInteger,
    val owner: StdAddr,
    val jetton: StdAddr,
    val lastTransactionLt: ULong,
    val codeHash: HashBytes? = null,
    val dataHash: HashBytes? = null
)

/**
 * Comprehensive block information from the v3 API.
 * 
 * Contains detailed metadata about a block including its position in the blockchain,
 * validation information, timing data, and references to related blocks.
 * 
 * @param workchain The workchain ID
 * @param shard The shard identifier
 * @param seqno The block sequence number
 * @param rootHash The block root hash
 * @param fileHash The block file hash
 * @param globalId The global blockchain ID
 * @param version The block format version
 * @param afterMerge Whether this block was created after a shard merge
 * @param beforeSplit Whether this block was created before a shard split
 * @param afterSplit Whether this block was created after a shard split
 * @param wantMerge Whether this block wants to merge with another shard
 * @param wantSplit Whether this block wants to split the shard
 * @param keyBlock Whether this is a key block (contains validator set changes)
 * @param vertSeqnoIncr Whether the vertical sequence number was incremented
 * @param flags Block flags
 * @param genUtime Block generation time (Unix timestamp as string)
 * @param startLt Starting logical time for this block
 * @param endLt Ending logical time for this block
 * @param validatorListHashShort Short hash of the validator list
 * @param genCatchainSeqno Catchain sequence number for block generation
 * @param minRefMcSeqno Minimum referenced masterchain sequence number
 * @param prevKeyBlockSeqno Previous key block sequence number
 * @param vertSeqno Vertical sequence number
 * @param masterRefSeqno Referenced masterchain sequence number
 * @param randSeed Random seed used in block generation
 * @param createdBy Identifier of the validator that created this block
 * @param txCount Number of transactions in this block
 * @param masterchainBlockRef Reference to the masterchain block
 * @param prevBlocks References to previous blocks
 */
@Serializable
data class Block(
    val workchain: Int,
    val shard: String,
    val seqno: UInt,
    @SerialName("root_hash") val rootHash: String,
    @SerialName("file_hash") val fileHash: String,
    @SerialName("global_id") val globalId: Int,
    val version: UInt,
    @SerialName("after_merge") val afterMerge: Boolean,
    @SerialName("before_split") val beforeSplit: Boolean,
    @SerialName("after_split") val afterSplit: Boolean,
    @SerialName("want_merge") val wantMerge: Boolean,
    @SerialName("want_split") val wantSplit: Boolean,
    @SerialName("key_block") val keyBlock: Boolean,
    @SerialName("vert_seqno_incr") val vertSeqnoIncr: Boolean,
    val flags: UByte,
    @SerialName("gen_utime") val genUtime: String,
    @SerialName("start_lt") val startLt: String,
    @SerialName("end_lt") val endLt: String,
    @SerialName("validator_list_hash_short") val validatorListHashShort: UInt,
    @SerialName("gen_catchain_seqno") val genCatchainSeqno: UInt,
    @SerialName("min_ref_mc_seqno") val minRefMcSeqno: UInt,
    @SerialName("prev_key_block_seqno") val prevKeyBlockSeqno: UInt,
    @SerialName("vert_seqno") val vertSeqno: UInt,
    @SerialName("master_ref_seqno") val masterRefSeqno: UInt,
    @SerialName("rand_seed") val randSeed: String,
    @SerialName("created_by") val createdBy: String,
    @SerialName("tx_count") val txCount: UInt,
    @SerialName("masterchain_block_ref") val masterchainBlockRef: BlockRef,
    @SerialName("prev_blocks") val prevBlocks: List<BlockRef>
)

/**
 * Reference to a block containing minimal identification information.
 * 
 * @param workchain The workchain ID
 * @param shard The shard identifier
 * @param seqno The block sequence number
 */
@Serializable
data class BlockRef(
    val workchain: Int,
    val shard: String,
    val seqno: UInt
)

@Serializable
data class Transaction(
    val account: StdAddr,
    val hash: HashBytes,
    val lt: ULong,
    val now: UInt,
    @SerialName("mc_block_seqno") val mcBlockSeqno: UInt,
    @SerialName("trace_id") val traceId: HashBytes,
    @SerialName("prev_trans_hash") val prevTransHash: HashBytes,
    @SerialName("prev_trans_lt") val prevTransLt: ULong,
    @SerialName("orig_status") val origStatus: AccountStatus,
    @SerialName("end_status") val endStatus: AccountStatus,
    @SerialName("total_fees") val totalFees: Tokens,
    @SerialName("total_fees_extra_currencies") val totalFeesExtraCurrencies: Map<String, String> = emptyMap(),
    val description: TxDescription,
    @SerialName("block_ref") val blockRef: BlockRef,
    @SerialName("in_msg") val inMsg: Message? = null,
    @SerialName("out_msgs") val outMsgs: List<Message>,
    @SerialName("account_state_before") val accountStateBefore: BriefAccountState,
    @SerialName("account_state_after") val accountStateAfter: BriefAccountState
)

@Serializable
data class BriefAccountState(
    val hash: HashBytes,
    val balance: Tokens? = null,
    val extraCurrencies: Map<String, String>? = null,
    val accountStatus: AccountStatus? = null,
    val frozenHash: HashBytes? = null,
    val dataHash: HashBytes? = null,
    val codeHash: HashBytes? = null
)

@Serializable
enum class AccountStatus {
    @SerialName("uninit") UNINIT,
    @SerialName("frozen") FROZEN,
    @SerialName("active") ACTIVE,
    @SerialName("nonexist") NONEXIST
}

/**
 * Sealed class representing different types of transaction descriptions.
 * 
 * Transaction descriptions contain detailed information about how a transaction
 * was processed, including the various phases of execution and their outcomes.
 */
@Serializable
sealed class TxDescription {
    /**
     * Ordinary transaction description for regular user transactions.
     * 
     * @param aborted Whether the transaction was aborted
     * @param destroyed Whether the account was destroyed
     * @param creditFirst Whether credit phase was executed first
     * @param storagePh Storage phase information (if executed)
     * @param creditPh Credit phase information (if executed)
     * @param computePh Compute phase information (always present)
     * @param action Action phase information (if executed)
     * @param bounce Bounce phase information (if executed)
     */
    @Serializable
    @SerialName("ord")
    data class Ordinary(
        val aborted: Boolean,
        val destroyed: Boolean,
        @SerialName("credit_first") val creditFirst: Boolean,
        @SerialName("storage_ph") val storagePh: TxDescriptionStoragePhase? = null,
        @SerialName("credit_ph") val creditPh: TxDescriptionCreditPhase? = null,
        @SerialName("compute_ph") val computePh: TxDescriptionComputePhase,
        val action: TxDescriptionActionPhase? = null,
        val bounce: TxDescriptionBouncePhase? = null
    ) : TxDescription()
    
    /**
     * Tick-tock transaction description for system transactions.
     * 
     * Tick-tock transactions are special system transactions that are executed
     * automatically by the blockchain to maintain system state.
     * 
     * @param aborted Whether the transaction was aborted
     * @param destroyed Whether the account was destroyed
     * @param isTock Whether this is a "tock" transaction (vs "tick")
     * @param storagePh Storage phase information
     * @param computePh Compute phase information
     * @param action Action phase information (if executed)
     */
    @Serializable
    @SerialName("tick_tock")
    data class TickTock(
        val aborted: Boolean,
        val destroyed: Boolean,
        @SerialName("is_tock") val isTock: Boolean,
        @SerialName("storage_ph") val storagePh: TxDescriptionStoragePhase,
        @SerialName("compute_ph") val computePh: TxDescriptionComputePhase,
        val action: TxDescriptionActionPhase? = null
    ) : TxDescription()
}

@Serializable
data class TxDescriptionStoragePhase(
    @SerialName("storage_fees_collected") val storageFeesCollected: String,
    @SerialName("status_change") val statusChange: String
)

@Serializable
enum class AccountStatusChange {
    @SerialName("unchanged") UNCHANGED,
    @SerialName("frozen") FROZEN,
    @SerialName("deleted") DELETED
}

@Serializable
data class TxDescriptionCreditPhase(
    val dueFeesCollected: Tokens? = null,
    val credit: Tokens
)

@Serializable
data class TxDescriptionComputePhase(
    val skipped: Boolean,
    val success: Boolean? = null,
    @SerialName("msg_state_used") val msgStateUsed: Boolean? = null,
    @SerialName("account_activated") val accountActivated: Boolean? = null,
    @SerialName("gas_fees") val gasFees: String? = null,
    @SerialName("gas_used") val gasUsed: String? = null,
    @SerialName("gas_limit") val gasLimit: String? = null,
    @SerialName("gas_credit") val gasCredit: String? = null,
    val mode: Byte? = null,
    @SerialName("exit_code") val exitCode: Int? = null,
    @SerialName("exit_arg") val exitArg: Int? = null,
    @SerialName("vm_steps") val vmSteps: UInt? = null,
    @SerialName("vm_init_state_hash") val vmInitStateHash: String? = null,
    @SerialName("vm_final_state_hash") val vmFinalStateHash: String? = null,
    val reason: String? = null
)

@Serializable
data class TxDescriptionActionPhase(
    val success: Boolean,
    val valid: Boolean,
    @SerialName("no_funds") val noFunds: Boolean,
    @SerialName("status_change") val statusChange: String,
    @SerialName("total_fwd_fees") val totalFwdFees: String? = null,
    @SerialName("total_action_fees") val totalActionFees: String? = null,
    @SerialName("result_code") val resultCode: Int,
    @SerialName("tot_actions") val totActions: UShort,
    @SerialName("spec_actions") val specActions: UShort,
    @SerialName("skipped_actions") val skippedActions: UShort,
    @SerialName("msgs_created") val msgsCreated: UShort,
    @SerialName("action_list_hash") val actionListHash: String,
    @SerialName("tot_msg_size") val totMsgSize: MessageSize
)

@Serializable
data class TxDescriptionBouncePhase(
    val type: String,
    val msgSize: MessageSize? = null,
    val reqFwdFees: Tokens? = null,
    val msgFees: Tokens? = null,
    val fwdFees: Tokens? = null
)

@Serializable
data class MessageSize(
    val cells: ULong,
    val bits: ULong
)

@Serializable
data class Message(
    val hash: HashBytes,
    val source: StdAddr? = null,
    val destination: StdAddr? = null,
    val value: Tokens? = null,
    @SerialName("value_extra_currencies") val valueExtraCurrencies: Map<String, String>? = null,
    @SerialName("fwd_fee") val fwdFee: Tokens? = null,
    @SerialName("ihr_fee") val ihrFee: Tokens? = null,
    @SerialName("created_lt") val createdLt: ULong? = null,
    @SerialName("created_at") val createdAt: UInt? = null,
    @SerialName("ihr_disabled") val ihrDisabled: Boolean? = null,
    val bounce: Boolean? = null,
    val bounced: Boolean? = null,
    @SerialName("import_fee") val importFee: Tokens? = null,
    @SerialName("message_content") val messageContent: MessageContent,
    @SerialName("init_state") val initState: MessageContent? = null,
    @SerialName("hash_norm") val hashNorm: HashBytes? = null
)

@Serializable
data class MessageContent(
    val hash: HashBytes,
    val body: String,
    val decoded: DecodedContent? = null
)

/**
 * Sealed class representing different types of decoded message content.
 * 
 * The v3 API can decode certain types of message content to provide
 * structured information about the message payload.
 */
@Serializable
sealed class DecodedContent {
    /**
     * Plain text message content.
     * 
     * @param text The decoded text content
     */
    @Serializable
    @SerialName("text")
    data class Text(val text: String) : DecodedContent()
    
    /**
     * Jetton transfer message content.
     * 
     * Represents a decoded Jetton transfer operation with all relevant parameters.
     * 
     * @param queryId Unique query identifier for the transfer
     * @param amount Amount of Jettons being transferred
     * @param destination Destination address for the Jettons
     * @param responseDestination Address to send response messages to
     * @param customPayload Custom payload data
     * @param forwardTonAmount Amount of TON to forward with the transfer
     * @param forwardPayload Payload to forward with the transfer
     */
    @Serializable
    @SerialName("jetton_transfer")
    data class JettonTransfer(
        val queryId: ULong,
        @Contextual val amount: BigInteger,
        val destination: StdAddr,
        val responseDestination: StdAddr? = null,
        val customPayload: String? = null,
        val forwardTonAmount: Tokens,
        val forwardPayload: String? = null
    ) : DecodedContent()
}

/**
 * Address book containing additional information about addresses referenced in responses.
 * 
 * The v3 API includes address books in responses to provide context about
 * addresses that appear in transactions and other data structures.
 * 
 * @param items Set of addresses with additional context information
 */
@Serializable
data class AddressBook(
    val items: Set<StdAddr> = emptySet()
)
