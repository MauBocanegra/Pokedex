package com.maubocanegra.pokedex.pokemon.data.repository

import android.content.Context
import androidx.collection.LruCache
import com.android.volley.RequestQueue
import com.maubocanegra.pokedex.pokemon.data.network.VolleyByteArrayRequest
import com.maubocanegra.pokedex.pokemon.domain.repository.ImageAsset
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume

private const val IMAGE_CACHE_DIR: String = "images"
private const val IMAGE_FILE_EXTENSION: String = ".bin"

class PokemonImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val requestQueue: RequestQueue,
    private val memoryCache: LruCache<String, ByteArray>
) : PokemonImageRepository {

    override suspend fun getImageFile(url: String): Result<ImageAsset> = withContext(Dispatchers.IO) {

        val targetFile = cacheFileFor(url)

        if (targetFile.exists()) {
            return@withContext Result.success(
                ImageAsset(
                    targetFile.absolutePath,
                    "image/png"
                )
            )
        }

        memoryCache[url]?.let { cachedBytes ->
            targetFile.writeBytes(cachedBytes)
            return@withContext Result.success(
                ImageAsset(
                    targetFile.absolutePath,
                    "image/png"
                )
            )
        }

        suspendCancellableCoroutine { continuation ->
            val request = VolleyByteArrayRequest(
                url = url,
                onSuccess = { bytes ->
                    memoryCache.put(url, bytes)
                    targetFile.writeBytes(bytes)
                    continuation.resume(
                        Result.success(
                            ImageAsset(
                                targetFile.absolutePath,
                                "image/png"
                            )
                        )
                    )
                },
                onError = { e -> continuation.resume(Result.failure(e)) }
            )
            requestQueue.add(request)
            continuation.invokeOnCancellation { request.cancel() }
        }
    }

    override suspend fun prefetch(
        urls: List<String>
    ) = coroutineScope {
        urls.forEach { url -> launch { getImageFile(url) } }
    }

    override suspend fun evict(url: String) {
        memoryCache.remove(url)
        cacheFileFor(url).delete()
    }

    private fun cacheFileFor(url: String): File {
        val directory = File(applicationContext.cacheDir, IMAGE_CACHE_DIR).apply { mkdirs() }
        val hashedName = sha256(url) + IMAGE_FILE_EXTENSION
        return File(directory, hashedName)
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return buildString(digest.size * 2) { digest.forEach { append("%02x".format(it)) } }
    }

    //private fun maxKilobytes(): Int = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
}