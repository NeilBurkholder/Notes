package com.ncautomation.notes.pro.dialogs

import android.content.DialogInterface.BUTTON_POSITIVE
import androidx.appcompat.app.AlertDialog
import com.ncautomation.commons.extensions.*
import com.ncautomation.commons.helpers.ensureBackgroundThread
import com.ncautomation.notes.pro.R
import com.ncautomation.notes.pro.activities.SimpleActivity
import com.ncautomation.notes.pro.databinding.DialogRenameNoteBinding
import com.ncautomation.notes.pro.extensions.config
import com.ncautomation.notes.pro.extensions.notesDB
import com.ncautomation.notes.pro.extensions.updateWidgets
import com.ncautomation.notes.pro.helpers.NotesHelper
import com.ncautomation.notes.pro.models.Note
import java.io.File

class RenameNoteDialog(val activity: SimpleActivity, val note: Note, val currentNoteText: String?, val callback: (note: Note) -> Unit) {

    init {
        val binding = DialogRenameNoteBinding.inflate(activity.layoutInflater)
        val view = binding.root
        binding.lockedNoteTitle.setText(note.title)

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok, null)
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(view, this, R.string.rename_note) { alertDialog ->
                    alertDialog.showKeyboard(binding.lockedNoteTitle)
                    alertDialog.getButton(BUTTON_POSITIVE).setOnClickListener {
                        val title = binding.lockedNoteTitle.value
                        ensureBackgroundThread {
                            newTitleConfirmed(title, alertDialog)
                        }
                    }
                }
            }
    }

    private fun newTitleConfirmed(title: String, dialog: AlertDialog) {
        when {
            title.isEmpty() -> activity.toast(R.string.no_title)
            activity.notesDB.getNoteIdWithTitleCaseSensitive(title) != null -> activity.toast(R.string.title_taken)
            else -> {
                note.title = title
                if (activity.config.autosaveNotes && currentNoteText != null) {
                    note.value = currentNoteText
                }

                val path = note.path
                if (path.isEmpty()) {
                    activity.notesDB.insertOrUpdate(note)
                    activity.runOnUiThread {
                        dialog.dismiss()
                        callback(note)
                    }
                } else {
                    if (title.isEmpty()) {
                        activity.toast(com.ncautomation.commons.R.string.filename_cannot_be_empty)
                        return
                    }

                    val file = File(path)
                    val newFile = File(file.parent, title)
                    if (!newFile.name.isAValidFilename()) {
                        activity.toast(com.ncautomation.commons.R.string.invalid_name)
                        return
                    }

                    activity.renameFile(file.absolutePath, newFile.absolutePath, false) { success, useAndroid30Way ->
                        if (success) {
                            note.path = newFile.absolutePath
                            NotesHelper(activity).insertOrUpdateNote(note) {
                                dialog.dismiss()
                                callback(note)
                            }
                        } else {
                            activity.toast(com.ncautomation.commons.R.string.rename_file_error)
                            return@renameFile
                        }
                    }
                }

                activity.baseContext.updateWidgets()
            }
        }
    }
}
