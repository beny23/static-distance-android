package com.equalexperts.client.softwaredesignsystems.eatout

import android.app.Application
import org.osmdroid.config.Configuration

class EatOutToHelpOutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().userAgentValue = packageName
    }
}