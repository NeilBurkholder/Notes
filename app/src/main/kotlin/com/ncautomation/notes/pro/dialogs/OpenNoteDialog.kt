package com.ncautomation.notes.pro.dialogs

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ncautomation.commons.activities.BaseSimpleActivity
import com.ncautomation.commons.extensions.getAlertDialogBuilder
import com.ncautomation.commons.extensions.setupDialogStuff
import com.ncautomation.commons.views.AutoStaggeredGridLayoutManager
import com.ncautomation.notes.pro.R
import com.ncautomation.notes.pro.adapters.OpenNoteAdapter
import com.ncautomation.notes.pro.databinding.DialogOpenNoteBinding
import com.ncautomation.notes.pro.helpers.NotesHelper
import com.ncautomation.notes.pro.models.Note

class OpenNoteDialog(val activity: BaseSimpleActivity, val callback: (checkedId: Long, newNote: Note?) -> Unit) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogOpenNoteBinding.inflate(activity.layoutInflater)

        val noteItemWidth = activity.resources.getDimensionPixelSize(R.dimen.grid_note_item_width)
        binding.dialogOpenNoteList.layoutManager = AutoStaggeredGridLayoutManager(noteItemWidth, StaggeredGridLayoutManager.VERTICAL)

        NotesHelper(activity).getNotes {
            initDialog(it, binding)
        }
    }

    private fun initDialog(notes: List<Note>, binding: DialogOpenNoteBinding) {
        binding.dialogOpenNoteList.adapter = OpenNoteAdapter(activity, notes, binding.dialogOpenNoteList) {
            it as Note
            callback(it.id!!, null)
            dialog?.dismiss()
        }

        binding.newNoteFab.setOnClickListener {
            NewNoteDialog(activity, setChecklistAsDefault = false) {
                callback(0, it)
                dialog?.dismiss()
            }
        }

        activity.getAlertDialogBuilder()
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.open_note) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }
}
