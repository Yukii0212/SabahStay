package com.example.testversion

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class SabahStayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
