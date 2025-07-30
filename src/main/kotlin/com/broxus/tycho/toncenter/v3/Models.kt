package com.broxus.tycho.toncenter.v3

import com.broxus.tycho.toncenter.common.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.math.BigInteger

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

@Serializable
data class TransactionsByMcBlockRequest(
    val seqno: UInt,
    val limit: UInt = 10u,
    val offset: UInt = 0u,
    val sort: SortDirection = SortDirection.DESC
)

@Serializable
data class AdjacentTransactionsRequest(
    val hash: HashBytes,
    val direction: MessageDirection? = null
)

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

@Serializable
data class JettonMastersRequest(
    val address: List<StdAddr>? = null,
    val adminAddress: List<StdAddr>? = null,
    val limit: UInt = 10u,
    val offset: UInt = 0u
)

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

@Serializable
enum class SortDirection {
    @SerialName("asc") ASC,
    @SerialName("desc") DESC
}

@Serializable
enum class MessageDirection {
    @SerialName("in") IN,
    @SerialName("out") OUT
}

@Serializable
data class MasterchainInfoResponse(
    val last: Block,
    val first: Block
)

@Serializable
data class BlocksResponse(
    val blocks: List<Block>
)

@Serializable
data class TransactionsResponse(
    val transactions: List<Transaction>,
    @SerialName("address_book") val addressBook: AddressBook
)

@Serializable
data class JettonMastersResponse(
    val jettonMasters: List<JettonMastersResponseItem>,
    val addressBook: AddressBook
)

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

@Serializable
data class JettonWalletsResponse(
    val jettonWallets: List<JettonWalletsResponseItem>,
    val addressBook: AddressBook
)

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

@Serializable
sealed class TxDescription {
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

@Serializable
sealed class DecodedContent {
    @Serializable
    @SerialName("text")
    data class Text(val text: String) : DecodedContent()
    
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

@Serializable
data class AddressBook(
    val items: Set<StdAddr> = emptySet()
)
