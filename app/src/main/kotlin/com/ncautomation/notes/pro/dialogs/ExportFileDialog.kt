package com.ncautomation.notes.pro.dialogs

import androidx.appcompat.app.AlertDialog
import com.ncautomation.commons.dialogs.FilePickerDialog
import com.ncautomation.commons.extensions.*
import com.ncautomation.notes.pro.R
import com.ncautomation.notes.pro.activities.SimpleActivity
import com.ncautomation.notes.pro.databinding.DialogExportFileBinding
import com.ncautomation.notes.pro.extensions.config
import com.ncautomation.notes.pro.models.Note
import java.io.File

class ExportFileDialog(val activity: SimpleActivity, val note: Note, val callback: (exportPath: String) -> Unit) {

    init {
        var realPath = File(note.path).parent ?: activity.config.lastUsedSavePath
        val binding = DialogExportFileBinding.inflate(activity.layoutInflater).apply {
            filePath.setText(activity.humanizePath(realPath))

            fileName.setText(note.title)
            extension.setText(activity.config.lastUsedExtension)
            filePath.setOnClickListener {
                FilePickerDialog(activity, realPath, false, false, true, true) {
                    filePath.setText(activity.humanizePath(it))
                    realPath = it
                }
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok, null)
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.export_as_file) { alertDialog ->
                    alertDialog.showKeyboard(binding.fileName)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val filename = binding.fileName.value
                        val extension = binding.extension.value

                        if (filename.isEmpty()) {
                            activity.toast(com.ncautomation.commons.R.string.filename_cannot_be_empty)
                            return@setOnClickListener
                        }

                        val fullFilename = if (extension.isEmpty()) filename else "$filename.$extension"
                        if (!fullFilename.isAValidFilename()) {
                            activity.toast(String.format(activity.getString(com.ncautomation.commons.R.string.filename_invalid_characters_placeholder, fullFilename)))
                            return@setOnClickListener
                        }

                        activity.config.lastUsedExtension = extension
                        activity.config.lastUsedSavePath = realPath
                        callback("$realPath/$fullFilename")
                        alertDialog.dismiss()
                    }
                }
            }
    }
}
