package com.maubocanegra.pokedex.pokemon.data.network

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

class VolleyByteArrayRequest(
    url: String,
    private val onSuccess: (ByteArray) -> Unit,
    private val onError: (Throwable) -> Unit
) : Request<ByteArray>(Method.GET, url, Response.ErrorListener { onError(it) }) {

    override fun getHeaders(): MutableMap<String, String> =
        mutableMapOf("Accept" to "image/*")

    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray> {
        val cacheEntry = HttpHeaderParser.parseCacheHeaders(response)
        return Response.success(response.data, cacheEntry)
    }

    override fun deliverResponse(response: ByteArray) = onSuccess(response)
}