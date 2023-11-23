package com.ncautomation.notes.pro.dialogs

import androidx.appcompat.app.AlertDialog
import com.ncautomation.commons.extensions.beVisible
import com.ncautomation.commons.extensions.getAlertDialogBuilder
import com.ncautomation.commons.extensions.setupDialogStuff
import com.ncautomation.notes.pro.R
import com.ncautomation.notes.pro.activities.SimpleActivity
import com.ncautomation.notes.pro.databinding.DialogDeleteNoteBinding
import com.ncautomation.notes.pro.models.Note

class DeleteNoteDialog(val activity: SimpleActivity, val note: Note, val callback: (deleteFile: Boolean) -> Unit) {
    var dialog: AlertDialog? = null

    init {
        val message = String.format(activity.getString(R.string.delete_note_prompt_message), note.title)
        val binding = DialogDeleteNoteBinding.inflate(activity.layoutInflater).apply{
            if (note.path.isNotEmpty()) {
                deleteNoteCheckbox.text = String.format(activity.getString(R.string.delete_file_itself), note.path)
                deleteNoteCheckboxHolder.beVisible()
                deleteNoteCheckboxHolder.setOnClickListener {
                    deleteNoteCheckbox.toggle()
                }
            }
            deleteNoteDescription.text = message
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.delete) { dialog, which -> dialogConfirmed(binding.deleteNoteCheckbox.isChecked) }
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed(deleteFile: Boolean) {
        callback(deleteFile && note.path.isNotEmpty())
        dialog?.dismiss()
    }
}
