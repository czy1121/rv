package com.demo.app

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        val app = this
        Coil.setImageLoader(ImageLoader.Builder(app).memoryCache {
            MemoryCache.Builder(app)
                .maxSizePercent(0.25)
                .build()
        }.diskCache {
            DiskCache.Builder()
                .directory(app.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }.fallback(ColorDrawable(Color.DKGRAY)).build())



    }
}