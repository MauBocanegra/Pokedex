package com.maubocanegra.pokedex.core.network.util

sealed class APIResult<out T> {

    data class Success<out T>(val data: T): APIResult<T>()

    data class Failure(
        val throwable: Throwable,
        val message: String? = throwable.localizedMessage,
        val code: Int? = null // HTTP error codes
    ) : APIResult <Nothing>()

    data object Loading : APIResult<Nothing>()

    val isSuccess get() = this is Success
    val isFailure get() = this is Failure

}
