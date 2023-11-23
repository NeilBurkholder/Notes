package com.ncautomation.notes.pro.interfaces

interface ChecklistItemsListener {
    fun refreshItems()

    fun saveChecklist(callback: () -> Unit = {})
}
