package com.ncautomation.notes.pro.dialogs

import androidx.appcompat.app.AlertDialog
import com.ncautomation.commons.extensions.*
import com.ncautomation.commons.helpers.PROTECTION_NONE
import com.ncautomation.commons.helpers.ensureBackgroundThread
import com.ncautomation.notes.pro.R
import com.ncautomation.notes.pro.activities.SimpleActivity
import com.ncautomation.notes.pro.databinding.DialogImportFolderBinding
import com.ncautomation.notes.pro.extensions.notesDB
import com.ncautomation.notes.pro.extensions.parseChecklistItems
import com.ncautomation.notes.pro.helpers.NotesHelper
import com.ncautomation.notes.pro.models.Note
import com.ncautomation.notes.pro.models.NoteType
import java.io.File

class ImportFolderDialog(val activity: SimpleActivity, val path: String, val callback: () -> Unit) : AlertDialog.Builder(activity) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogImportFolderBinding.inflate(activity.layoutInflater).apply {
            openFileFilename.setText(activity.humanizePath(path))
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok, null)
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.import_folder) { alertDialog ->
                    dialog = alertDialog
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val updateFilesOnEdit = binding.openFileType.checkedRadioButtonId == R.id.open_file_update_file
                        ensureBackgroundThread {
                            saveFolder(updateFilesOnEdit)
                        }
                    }
                }
            }
    }

    private fun saveFolder(updateFilesOnEdit: Boolean) {
        val folder = File(path)
        folder.listFiles { file ->
            val filename = file.path.getFilenameFromPath()
            when {
                file.isDirectory -> false
                filename.isMediaFile() -> false
                file.length() > 1000 * 1000 -> false
                activity.notesDB.getNoteIdWithTitle(filename) != null -> false
                else -> true
            }
        }?.forEach {
            val storePath = if (updateFilesOnEdit) it.absolutePath else ""
            val title = it.absolutePath.getFilenameFromPath()
            val value = if (updateFilesOnEdit) "" else it.readText()
            val fileText = it.readText().trim()
            val checklistItems = fileText.parseChecklistItems()
            if (checklistItems != null) {
                saveNote(title.substringBeforeLast('.'), fileText, NoteType.TYPE_CHECKLIST, "")
            } else {
                if (updateFilesOnEdit) {
                    activity.handleSAFDialog(path) {
                        saveNote(title, value, NoteType.TYPE_TEXT, storePath)
                    }
                } else {
                    saveNote(title, value, NoteType.TYPE_TEXT, storePath)
                }
            }
        }

        activity.runOnUiThread {
            callback()
            dialog?.dismiss()
        }
    }

    private fun saveNote(title: String, value: String, type: NoteType, path: String) {
        val note = Note(null, title, value, type, path, PROTECTION_NONE, "")
        NotesHelper(activity).insertOrUpdateNote(note)
    }
}
