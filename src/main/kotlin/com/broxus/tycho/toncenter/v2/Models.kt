package com.broxus.tycho.toncenter.v2

import com.broxus.tycho.toncenter.common.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
data class EmptyParams(val dummy: String = "")

@Serializable
data class GetShardsParams(val seqno: UInt)

@Serializable
data class DetectAddressParams(val address: String)

@Serializable
data class AccountParams(val address: StdAddr)

@Serializable
data class BlockHeaderParams(
    val workchain: Int,
    val shard: Long,
    val seqno: UInt,
    val rootHash: HashBytes? = null,
    val fileHash: HashBytes? = null
)

@Serializable
data class TransactionsParams(
    val address: StdAddr,
    val limit: UByte = 10u,
    val lt: ULong? = null,
    val hash: HashBytes? = null,
    val toLt: ULong = 0u
)

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

@Serializable
data class SendBocParams(val boc: String)

@Serializable
data class RunGetMethodParams(
    val address: StdAddr,
    val method: String,
    val stack: List<TonlibInputStackItem>
)

@Serializable
sealed class TonlibInputStackItem {
    @Serializable
    @SerialName("num")
    data class Num(val value: String) : TonlibInputStackItem()
    
    @Serializable
    @SerialName("cell")
    data class Cell(val bytes: String) : TonlibInputStackItem()
    
    @Serializable
    @SerialName("slice")
    data class Slice(val bytes: String) : TonlibInputStackItem()
}

@Serializable
data class MasterchainInfoResponseWrapper(
    val result: MasterchainInfoResponse,
    val ok: Boolean
)

@Serializable
data class MasterchainInfoResponse(
    @SerialName("@type") val type: String,
    val last: TonlibBlockId,
    @SerialName("state_root_hash") val stateRootHash: String,
    val init: TonlibBlockId,
    @SerialName("@extra") val extra: String = ""
)

@Serializable
data class TonlibBlockId(
    @SerialName("@type") val type: String = "ton.blockIdExt",
    val workchain: Int,
    val shard: String,
    val seqno: UInt,
    @SerialName("root_hash") val rootHash: String,
    @SerialName("file_hash") val fileHash: String
)

@Serializable
data class ShardsResponse(
    @SerialName("@type") val type: String,
    val shards: List<TonlibBlockId>,
    @SerialName("@extra") val extra: String = ""
)

@Serializable
data class AddressFormsResponse(
    val rawForm: StdAddr,
    val bounceable: Base64Form,
    val nonBounceable: Base64Form,
    val givenType: AddressType,
    val testOnly: Boolean
)

@Serializable
data class Base64Form(
    val b64: String,
    val b64url: String
)

@Serializable
enum class AddressType {
    @SerialName("raw_form") RAW_FORM,
    @SerialName("friendly_bounceable") FRIENDLY_BOUNCEABLE,
    @SerialName("friendly_non_bounceable") FRIENDLY_NON_BOUNCEABLE
}

@Serializable
data class AddressInformationResponseWrapper(
    val result: AddressInformationResponse,
    val ok: Boolean
)

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

@Serializable
enum class TonlibAccountStatus {
    @SerialName("uninitialized") UNINITIALIZED,
    @SerialName("frozen") FROZEN,
    @SerialName("active") ACTIVE
}

@Serializable
data class TonlibTransactionId(
    val lt: ULong,
    val hash: HashBytes
) {
    companion object {
        fun default() = TonlibTransactionId(0u, HashBytes.ZERO)
    }
}

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

@Serializable
data class TonlibAddress(
    @SerialName("@type") val type: String = "accountAddress",
    val accountAddress: StdAddr
)

@Serializable
sealed class ParsedAccountState {
    @Serializable
    @SerialName("uninit")
    data class Uninit(val frozenHash: HashBytes? = null) : ParsedAccountState()
    
    @Serializable
    @SerialName("raw")
    data class Raw(
        val code: String? = null,
        val data: String? = null,
        val frozenHash: HashBytes? = null
    ) : ParsedAccountState()
    
    @Serializable
    @SerialName("wallet_v3")
    data class WalletV3(
        val seqno: UInt,
        val publicKey: String,
        val walletId: UInt
    ) : ParsedAccountState()
    
    @Serializable
    @SerialName("wallet_v4")
    data class WalletV4(
        val seqno: UInt,
        val publicKey: String,
        val walletId: UInt
    ) : ParsedAccountState()
}

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

@Serializable
sealed class TokenData {
    @Serializable
    @SerialName("jetton_master")
    data class JettonMaster(
        @Contextual val totalSupply: BigInteger,
        val mintable: Boolean,
        val adminAddress: StdAddr? = null,
        val jettonContent: JettonContent,
        val jettonWalletCode: String
    ) : TokenData()
    
    @Serializable
    @SerialName("jetton_wallet")
    data class JettonWallet(
        @Contextual val balance: BigInteger,
        val owner: StdAddr,
        val jetton: StdAddr,
        val jettonWalletCode: String
    ) : TokenData()
}

@Serializable
sealed class JettonContent {
    @Serializable
    @SerialName("onchain")
    data class Onchain(val data: Map<String, String>) : JettonContent()
    
    @Serializable
    @SerialName("offchain")
    data class Offchain(val data: String) : JettonContent()
}

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
