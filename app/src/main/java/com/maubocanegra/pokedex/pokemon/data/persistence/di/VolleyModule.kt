package com.maubocanegra.pokedex.pokemon.data.persistence.di

import android.content.Context
import androidx.collection.LruCache
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
object VolleyModule {
    @Provides @Singleton
    fun provideDiskCache(@ApplicationContext context: Context): DiskBasedCache {
        val cacheDir = File(context.cacheDir, "volley")
        return DiskBasedCache(
            cacheDir,
            50 * 1024 * 1024 // 50MB
        )
    }

    @Provides @Singleton
    fun provideRequestQueue(diskCache: DiskBasedCache): RequestQueue {
        return RequestQueue(diskCache, BasicNetwork(HurlStack())).apply {
            start()
        }
    }

    @Provides
    @Singleton
    fun provideByteArrayMemoryCache(): LruCache<String, ByteArray> {
        val maxKilobytes = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
        return object : LruCache<String, ByteArray>(maxKilobytes) {
            override fun sizeOf(key: String, value: ByteArray): Int = value.size / 1024
        }
    }
}