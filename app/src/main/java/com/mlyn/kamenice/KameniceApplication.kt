package com.mlyn.kamenice

import android.app.Application
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen

class KameniceApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}