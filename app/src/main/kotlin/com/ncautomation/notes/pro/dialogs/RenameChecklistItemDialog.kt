package com.ncautomation.notes.pro.dialogs

import android.app.Activity
import android.content.DialogInterface.BUTTON_POSITIVE
import com.ncautomation.commons.extensions.*
import com.ncautomation.notes.pro.databinding.DialogRenameChecklistItemBinding

class RenameChecklistItemDialog(val activity: Activity, val oldTitle: String, callback: (newTitle: String) -> Unit) {
    init {
        val binding = DialogRenameChecklistItemBinding.inflate(activity.layoutInflater).apply {
            checklistItemTitle.setText(oldTitle)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok, null)
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    alertDialog.showKeyboard(binding.checklistItemTitle)
                    alertDialog.getButton(BUTTON_POSITIVE).setOnClickListener {
                        val newTitle = binding.checklistItemTitle.value
                        when {
                            newTitle.isEmpty() -> activity.toast(com.ncautomation.commons.R.string.empty_name)
                            else -> {
                                callback(newTitle)
                                alertDialog.dismiss()
                            }
                        }
                    }
                }
            }
    }
}
