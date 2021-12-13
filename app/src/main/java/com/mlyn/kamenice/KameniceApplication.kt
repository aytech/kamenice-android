package com.mlyn.kamenice

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

@Suppress("unused")
class KameniceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}