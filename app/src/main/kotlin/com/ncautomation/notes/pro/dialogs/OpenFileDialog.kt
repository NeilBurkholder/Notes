package com.ncautomation.notes.pro.dialogs

import androidx.appcompat.app.AlertDialog
import com.ncautomation.commons.extensions.getAlertDialogBuilder
import com.ncautomation.commons.extensions.getFilenameFromPath
import com.ncautomation.commons.extensions.humanizePath
import com.ncautomation.commons.extensions.setupDialogStuff
import com.ncautomation.commons.helpers.PROTECTION_NONE
import com.ncautomation.notes.pro.R
import com.ncautomation.notes.pro.activities.SimpleActivity
import com.ncautomation.notes.pro.databinding.DialogOpenFileBinding
import com.ncautomation.notes.pro.models.Note
import com.ncautomation.notes.pro.models.NoteType
import java.io.File

class OpenFileDialog(val activity: SimpleActivity, val path: String, val callback: (note: Note) -> Unit) : AlertDialog.Builder(activity) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogOpenFileBinding.inflate(activity.layoutInflater).apply {
            openFileFilename.setText(activity.humanizePath(path))
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok, null)
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.open_file) { alertDialog ->
                    dialog = alertDialog
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val updateFileOnEdit = binding.openFileType.checkedRadioButtonId == binding.openFileUpdateFile.id
                        val storePath = if (updateFileOnEdit) path else ""
                        val storeContent = if (updateFileOnEdit) "" else File(path).readText()

                        if (updateFileOnEdit) {
                            activity.handleSAFDialog(path) {
                                saveNote(storeContent, storePath)
                            }
                        } else {
                            saveNote(storeContent, storePath)
                        }
                    }
                }
            }
    }

    private fun saveNote(storeContent: String, storePath: String) {
        val filename = path.getFilenameFromPath()
        val note = Note(null, filename, storeContent, NoteType.TYPE_TEXT, storePath, PROTECTION_NONE, "")
        callback(note)
        dialog?.dismiss()
    }
}
