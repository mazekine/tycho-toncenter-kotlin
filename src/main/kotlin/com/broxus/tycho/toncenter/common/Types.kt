package com.broxus.tycho.toncenter.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

/**
 * Represents a standard TON blockchain address in workchain:address format.
 * 
 * TON addresses consist of a workchain ID (typically -1 for masterchain or 0 for basechain)
 * and a 64-character hexadecimal address string. This class provides parsing and serialization
 * capabilities for working with TON addresses in API requests and responses.
 * 
 * ## Examples
 * ```kotlin
 * val address = StdAddr.parse("0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8")
 * println(address.workchain) // 0
 * println(address.address)   // "83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8"
 * println(address.toString()) // "0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8"
 * ```
 * 
 * @param workchain The workchain ID (-1 for masterchain, 0 for basechain)
 * @param address The 64-character hexadecimal address string
 */
@Serializable(with = StdAddrSerializer::class)
data class StdAddr(
    val workchain: Int,
    val address: String
) {
    /**
     * Returns the address in standard workchain:address format.
     */
    override fun toString(): String = "${workchain}:${address}"
    
    companion object {
        /**
         * Parses a TON address from its string representation.
         * 
         * The address must be in the format "workchain:address" where workchain is an integer
         * and address is a hexadecimal string.
         * 
         * @param address The address string to parse
         * @return A StdAddr instance
         * @throws IllegalArgumentException if the address format is invalid
         */
        fun parse(address: String): StdAddr {
            val parts = address.split(":")
            require(parts.size == 2) { "Invalid address format" }
            return StdAddr(parts[0].toInt(), parts[1])
        }
    }
}

/**
 * Custom serializer for StdAddr that serializes to/from the standard workchain:address string format.
 * 
 * This serializer ensures that StdAddr instances are represented as simple strings in JSON,
 * making the API responses more readable and compatible with other TON ecosystem tools.
 */
object StdAddrSerializer : KSerializer<StdAddr> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StdAddr", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: StdAddr) {
        encoder.encodeString(value.toString())
    }
    
    override fun deserialize(decoder: Decoder): StdAddr {
        return StdAddr.parse(decoder.decodeString())
    }
}

/**
 * Represents a hash value as a hexadecimal string, typically used for transaction hashes,
 * block hashes, and other cryptographic identifiers in the TON blockchain.
 * 
 * @param value The hexadecimal hash string
 */
@Serializable(with = HashBytesSerializer::class)
data class HashBytes(val value: String) {
    companion object {
        /** A zero hash consisting of 64 zero characters */
        val ZERO = HashBytes("0".repeat(64))
    }
}

/**
 * Custom serializer for HashBytes that serializes to/from hexadecimal strings.
 * 
 * This serializer ensures that hash values are represented as simple strings in JSON
 * rather than objects with a "value" field.
 */
object HashBytesSerializer : KSerializer<HashBytes> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HashBytes", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: HashBytes) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): HashBytes {
        return HashBytes(decoder.decodeString())
    }
}

/**
 * Represents token amounts in the TON blockchain using BigInteger for precise arithmetic.
 * 
 * TON uses very large numbers for token amounts (measured in nanotons), so BigInteger
 * is necessary to avoid precision loss. This class provides saturating arithmetic
 * operations that prevent negative balances.
 * 
 * ## Saturating Arithmetic
 * The arithmetic operations in this class use "saturating" behavior, meaning that
 * operations that would result in negative values are clamped to zero instead.
 * This is useful for balance calculations where negative balances are not meaningful.
 * 
 * @param value The token amount as a BigInteger
 */
@Serializable(with = TokensSerializer::class)
data class Tokens(val value: BigInteger) {
    companion object {
        /** Zero tokens */
        val ZERO = Tokens(BigInteger.ZERO)
    }
    
    /**
     * Adds another token amount to this one.
     * 
     * @param other The token amount to add
     * @return A new Tokens instance with the sum
     */
    fun saturatingAdd(other: Tokens): Tokens {
        return Tokens(value + other.value)
    }
    
    /**
     * Subtracts another token amount from this one, with saturation at zero.
     * 
     * If the subtraction would result in a negative value, returns ZERO instead.
     * This prevents negative token balances which are not meaningful in most contexts.
     * 
     * @param other The token amount to subtract
     * @return A new Tokens instance with the difference, or ZERO if the result would be negative
     */
    fun saturatingSub(other: Tokens): Tokens {
        val result = value - other.value
        return if (result < BigInteger.ZERO) ZERO else Tokens(result)
    }
}

/**
 * Custom serializer for Tokens that serializes BigInteger values to/from strings.
 * 
 * This serializer ensures that large token amounts are represented as strings in JSON
 * to avoid precision loss that could occur with floating-point representations.
 */
object TokensSerializer : KSerializer<Tokens> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Tokens", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Tokens) {
        encoder.encodeString(value.value.toString())
    }
    
    override fun deserialize(decoder: Decoder): Tokens {
        return Tokens(BigInteger(decoder.decodeString()))
    }
}

/**
 * Represents a complete block identifier with all necessary hashes.
 * 
 * @param workchain The workchain ID
 * @param shard The shard identifier
 * @param seqno The sequence number
 * @param rootHash The root hash of the block
 * @param fileHash The file hash of the block
 */
@Serializable
data class BlockId(
    val workchain: Int,
    val shard: Long,
    val seqno: UInt,
    val rootHash: HashBytes,
    val fileHash: HashBytes
)

/**
 * Represents a shortened block identifier without hashes.
 * 
 * @param workchain The workchain ID
 * @param shard The shard identifier
 * @param seqno The sequence number
 */
@Serializable
data class BlockIdShort(
    val workchain: Int,
    val shard: Long,
    val seqno: UInt
)

/**
 * Custom serializer for BigInteger values used with @Contextual annotation.
 * 
 * This serializer is registered in the SerializersModule and used for BigInteger
 * fields marked with @Contextual. It ensures that large numbers are serialized
 * as strings to maintain precision across JSON serialization boundaries.
 */
object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeString(value.toString())
    }
    
    override fun deserialize(decoder: Decoder): BigInteger {
        return BigInteger(decoder.decodeString())
    }
}

/**
 * Represents a shard identifier in the TON blockchain.
 * 
 * Shards are used to partition the blockchain state across multiple chains
 * for scalability. Each shard is identified by a workchain and prefix.
 * 
 * @param workchain The workchain ID
 * @param prefix The shard prefix
 */
@Serializable
data class ShardIdent(
    val workchain: Int,
    val prefix: Long
) {
    companion object {
        /** The masterchain shard identifier */
        val MASTERCHAIN = ShardIdent(-1, Long.MIN_VALUE)
    }
}
