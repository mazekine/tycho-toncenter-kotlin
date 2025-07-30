package com.broxus.tycho.toncenter.v3

import com.broxus.tycho.toncenter.common.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    val addressBook: AddressBook
)

@Serializable
data class JettonMastersResponse(
    val jettonMasters: List<JettonMastersResponseItem>,
    val addressBook: AddressBook
)

@Serializable
data class JettonMastersResponseItem(
    val address: StdAddr,
    val totalSupply: BigInteger,
    val mintable: Boolean,
    val adminAddress: StdAddr? = null,
    val jettonContent: Map<String, Any>? = null,
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
    val balance: BigInteger,
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
    val rootHash: HashBytes,
    val fileHash: HashBytes,
    val globalId: Int,
    val version: UInt,
    val afterMerge: Boolean,
    val beforeSplit: Boolean,
    val afterSplit: Boolean,
    val wantMerge: Boolean,
    val wantSplit: Boolean,
    val keyBlock: Boolean,
    val vertSeqnoIncr: Boolean,
    val flags: UByte,
    val genUtime: UInt,
    val startLt: ULong,
    val endLt: ULong,
    val validatorListHashShort: UInt,
    val genCatchainSeqno: UInt,
    val minRefMcSeqno: UInt,
    val prevKeyBlockSeqno: UInt,
    val vertSeqno: UInt,
    val masterRefSeqno: UInt,
    val randSeed: HashBytes,
    val createdBy: HashBytes,
    val txCount: UInt,
    val masterchainBlockRef: BlockRef,
    val prevBlocks: List<BlockRef>
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
    val mcBlockSeqno: UInt,
    val traceId: HashBytes,
    val prevTransHash: HashBytes,
    val prevTransLt: ULong,
    val origStatus: AccountStatus,
    val endStatus: AccountStatus,
    val totalFees: Tokens,
    val totalFeesExtraCurrencies: Map<String, String> = emptyMap(),
    val description: TxDescription,
    val blockRef: BlockRef,
    val inMsg: Message? = null,
    val outMsgs: List<Message>,
    val accountStateBefore: BriefAccountState,
    val accountStateAfter: BriefAccountState
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
        val creditFirst: Boolean,
        val storagePh: TxDescriptionStoragePhase? = null,
        val creditPh: TxDescriptionCreditPhase? = null,
        val computePh: TxDescriptionComputePhase,
        val action: TxDescriptionActionPhase? = null,
        val bounce: TxDescriptionBouncePhase? = null
    ) : TxDescription()
    
    @Serializable
    @SerialName("tick_tock")
    data class TickTock(
        val aborted: Boolean,
        val destroyed: Boolean,
        val isTock: Boolean,
        val storagePh: TxDescriptionStoragePhase,
        val computePh: TxDescriptionComputePhase,
        val action: TxDescriptionActionPhase? = null
    ) : TxDescription()
}

@Serializable
data class TxDescriptionStoragePhase(
    val storageFeesCollected: Tokens,
    val statusChange: AccountStatusChange
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
sealed class TxDescriptionComputePhase {
    @Serializable
    data class Skipped(
        val skipped: Boolean = true,
        val reason: String
    ) : TxDescriptionComputePhase()
    
    @Serializable
    data class Executed(
        val skipped: Boolean = false,
        val success: Boolean,
        val msgStateUsed: Boolean,
        val accountActivated: Boolean,
        val gasFees: Tokens,
        val gasUsed: ULong,
        val gasLimit: ULong,
        val gasCredit: UInt? = null,
        val mode: Byte,
        val exitCode: Int,
        val vmSteps: UInt,
        val vmInitStateHash: HashBytes,
        val vmFinalStateHash: HashBytes
    ) : TxDescriptionComputePhase()
}

@Serializable
data class TxDescriptionActionPhase(
    val success: Boolean,
    val valid: Boolean,
    val noFunds: Boolean,
    val statusChange: AccountStatusChange,
    val totalFwdFees: Tokens? = null,
    val totalActionFees: Tokens? = null,
    val resultCode: Int,
    val totActions: UShort,
    val specActions: UShort,
    val skippedActions: UShort,
    val msgsCreated: UShort,
    val actionListHash: HashBytes,
    val totMsgSize: MessageSize
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
    val valueExtraCurrencies: Map<String, String>? = null,
    val fwdFee: Tokens? = null,
    val ihrFee: Tokens? = null,
    val createdLt: ULong? = null,
    val createdAt: UInt? = null,
    val ihrDisabled: Boolean? = null,
    val bounce: Boolean? = null,
    val bounced: Boolean? = null,
    val importFee: Tokens? = null,
    val messageContent: MessageContent,
    val initState: MessageContent? = null,
    val hashNorm: HashBytes? = null
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
        val amount: BigInteger,
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
