package com.ncautomation.notes.pro.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.ncautomation.notes.pro.adapters.WidgetAdapter

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = WidgetAdapter(applicationContext, intent)
}
