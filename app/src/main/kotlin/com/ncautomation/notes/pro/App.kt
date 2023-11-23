package com.ncautomation.notes.pro

import android.app.Application
import com.ncautomation.commons.extensions.checkUseEnglish

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
    }
}
