package utils

import java.io.Closeable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

val Any?.isNull
    get() = this == null

val Any?.isNotNull
    get() = !this.isNull

@ExperimentalContracts
inline fun <T : Closeable?, R> T.useMany(block: (T) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    try {
        return block(this)
    } catch (e: Exception) {
        throw e
    }
}