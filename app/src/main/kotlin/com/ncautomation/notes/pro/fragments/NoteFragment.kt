package com.ncautomation.notes.pro.fragments

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ncautomation.commons.extensions.*
import com.ncautomation.commons.helpers.PROTECTION_NONE
import com.ncautomation.notes.pro.activities.MainActivity
import com.ncautomation.notes.pro.extensions.config
import com.ncautomation.notes.pro.extensions.getPercentageFontSize
import com.ncautomation.notes.pro.helpers.NotesHelper
import com.ncautomation.notes.pro.models.Note

abstract class NoteFragment : Fragment() {
    protected var note: Note? = null
    var shouldShowLockedContent = false

    protected fun setupLockedViews(binding: CommonNoteBinding, note: Note) {
        binding.apply {
            noteLockedLayout.beVisibleIf(note.isLocked() && !shouldShowLockedContent)
            noteLockedImage.applyColorFilter(requireContext().getProperTextColor())

            noteLockedLabel.setTextColor(requireContext().getProperTextColor())
            noteLockedLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.root.context.getPercentageFontSize())

            noteLockedShow.underlineText()
            noteLockedShow.setTextColor(requireContext().getProperPrimaryColor())
            noteLockedShow.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.root.context.getPercentageFontSize())
            noteLockedShow.setOnClickListener {
                handleUnlocking()
            }
        }
    }

    protected fun saveNoteValue(note: Note, content: String?) {
        if (note.path.isEmpty()) {
            NotesHelper(requireActivity()).insertOrUpdateNote(note) {
                (activity as? com.ncautomation.notes.pro.activities.MainActivity)?.noteSavedSuccessfully(note.title)
            }
        } else {
            if (content != null) {
                val displaySuccess = activity?.config?.displaySuccess ?: false
                (activity as? com.ncautomation.notes.pro.activities.MainActivity)?.tryExportNoteValueToFile(note.path, note.title, content, displaySuccess)
            }
        }
    }

    fun handleUnlocking(callback: (() -> Unit)? = null) {
        if (callback != null && (note!!.protectionType == PROTECTION_NONE || shouldShowLockedContent)) {
            callback()
            return
        }

        activity?.performSecurityCheck(
            protectionType = note!!.protectionType,
            requiredHash = note!!.protectionHash,
            successCallback = { _, _ ->
                shouldShowLockedContent = true
                checkLockState()
                callback?.invoke()
            }
        )
    }

    fun updateNoteValue(value: String) {
        note?.value = value
    }

    fun updateNotePath(path: String) {
        note?.path = path
    }

    abstract fun checkLockState()

    interface CommonNoteBinding {
        val root: View
        val noteLockedLayout: View
        val noteLockedImage: ImageView
        val noteLockedLabel: TextView
        val noteLockedShow: TextView
    }
}
