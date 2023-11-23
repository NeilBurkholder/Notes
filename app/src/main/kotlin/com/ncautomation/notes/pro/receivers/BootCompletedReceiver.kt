package com.ncautomation.notes.pro.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ncautomation.commons.helpers.ensureBackgroundThread
import com.ncautomation.notes.pro.extensions.checkAndBackupNotesOnBoot

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        ensureBackgroundThread {
            context.apply {
                checkAndBackupNotesOnBoot()
            }
        }
    }
}
