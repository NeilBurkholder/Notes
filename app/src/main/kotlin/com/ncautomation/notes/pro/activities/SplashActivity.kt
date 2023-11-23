package com.ncautomation.notes.pro.activities

import android.content.Intent
import com.ncautomation.commons.activities.BaseSplashActivity
import com.ncautomation.notes.pro.helpers.OPEN_NOTE_ID

class SplashActivity : BaseSplashActivity() {
    override fun initActivity() {
        if (intent.extras?.containsKey(OPEN_NOTE_ID) == true) {
            Intent(this, com.ncautomation.notes.pro.activities.MainActivity::class.java).apply {
                putExtra(OPEN_NOTE_ID, intent.getLongExtra(OPEN_NOTE_ID, -1L))
                startActivity(this)
            }
        } else {
            startActivity(Intent(this, com.ncautomation.notes.pro.activities.MainActivity::class.java))
        }
        finish()
    }
    class Orange : BaseSplashActivity() {
        override fun initActivity() {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
