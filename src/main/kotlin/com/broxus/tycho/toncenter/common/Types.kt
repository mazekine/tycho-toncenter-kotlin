package com.broxus.tycho.toncenter.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

@Serializable(with = StdAddrSerializer::class)
data class StdAddr(
    val workchain: Int,
    val address: String
) {
    override fun toString(): String = "${workchain}:${address}"
    
    companion object {
        fun parse(address: String): StdAddr {
            val parts = address.split(":")
            require(parts.size == 2) { "Invalid address format" }
            return StdAddr(parts[0].toInt(), parts[1])
        }
    }
}

object StdAddrSerializer : KSerializer<StdAddr> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StdAddr", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: StdAddr) {
        encoder.encodeString(value.toString())
    }
    
    override fun deserialize(decoder: Decoder): StdAddr {
        return StdAddr.parse(decoder.decodeString())
    }
}

@Serializable(with = HashBytesSerializer::class)
data class HashBytes(val value: String) {
    companion object {
        val ZERO = HashBytes("0".repeat(64))
    }
}

object HashBytesSerializer : KSerializer<HashBytes> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HashBytes", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: HashBytes) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): HashBytes {
        return HashBytes(decoder.decodeString())
    }
}

@Serializable(with = TokensSerializer::class)
data class Tokens(val value: BigInteger) {
    companion object {
        val ZERO = Tokens(BigInteger.ZERO)
    }
    
    fun saturatingAdd(other: Tokens): Tokens {
        return Tokens(value + other.value)
    }
    
    fun saturatingSub(other: Tokens): Tokens {
        val result = value - other.value
        return if (result < BigInteger.ZERO) ZERO else Tokens(result)
    }
}

object TokensSerializer : KSerializer<Tokens> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Tokens", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Tokens) {
        encoder.encodeString(value.value.toString())
    }
    
    override fun deserialize(decoder: Decoder): Tokens {
        return Tokens(BigInteger(decoder.decodeString()))
    }
}

@Serializable
data class BlockId(
    val workchain: Int,
    val shard: Long,
    val seqno: UInt,
    val rootHash: HashBytes,
    val fileHash: HashBytes
)

@Serializable
data class BlockIdShort(
    val workchain: Int,
    val shard: Long,
    val seqno: UInt
)

@Serializable
data class ShardIdent(
    val workchain: Int,
    val prefix: Long
) {
    companion object {
        val MASTERCHAIN = ShardIdent(-1, -9223372036854775808L)
    }
}
