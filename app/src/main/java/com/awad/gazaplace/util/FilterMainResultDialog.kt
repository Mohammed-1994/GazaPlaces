package com.awad.gazaplace.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.awad.gazaplace.R
import com.awad.gazaplace.databinding.FilterMainResultDialogBinding
import com.awad.gazaplace.databinding.SearchAreaDialogBinding

class FilterMainResultDialog(var dialogType: Int) : DialogFragment() {
    companion object {
        const val TAG = "FilterDialog, myTag"
    }

    private lateinit var listener: NoticeDialogListener
    private lateinit var view: ConstraintLayout
    private lateinit var editText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

//        val textWatcher  = TextInvalidator()
//        dialogViewBinding.areaEditText.addTextChangedListener(textWatcher)

        Log.d(TAG, "onCreateDialog: ")
        return activity?.let {
            val builder = AlertDialog.Builder(it)


            // Get the layout inflater
            if (dialogType == Constants.FILTER_MAIN_RESULT_OPTION) {
                val dialogViewBinding = FilterMainResultDialogBinding.inflate(layoutInflater)
                view = dialogViewBinding.root
            } else if (dialogType == Constants.SEARCH_AREA_OPTION) {
                val dialogViewBinding = SearchAreaDialogBinding.inflate(layoutInflater)

                view = dialogViewBinding.root
                editText = dialogViewBinding.areaEditText

            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(
                    R.string.okay
                ) { _, _ ->
                    if (dialogType == Constants.SEARCH_AREA_OPTION) {
                        try {
                            val d = editText.text.toString().toDouble()
                            listener.onDialogPositiveClick(dialogType, d * 1000)
                        } catch (e: NumberFormatException) {
                            val d = 5.0
                            listener.onDialogPositiveClick(dialogType, d * 1000)
                        }

                    } else if (dialogType == Constants.FILTER_MAIN_RESULT_OPTION) {
                        listener.onDialogPositiveClick(dialogType, -1.0)
                    }
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick(this)

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }
}

interface NoticeDialogListener {
    fun onDialogPositiveClick(dialogType: Int, distance: Double)
    fun onDialogNegativeClick(dialog: DialogFragment)
}
