package com.awad.gazaplace.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.awad.gazaplace.R
import com.awad.gazaplace.databinding.FilterMainResultDialogBinding
import com.awad.gazaplace.databinding.SearchAreaDialogBinding


class FilterMainResultDialog : DialogFragment() {
    companion object {
        const val TAG = "FilterDialog, myTag"
    }

    private var dialogType = 0
    private lateinit var listener: NoticeDialogListener
    private lateinit var view: ConstraintLayout
    private lateinit var editText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private var city = ""
    private var type = ""


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        dialogType = arguments?.getInt("dialog_type")!!

        Log.d(TAG, "onCreateDialog: ")
        return activity?.let {
            val builder = AlertDialog.Builder(it)


            // Get the layout inflater
            if (dialogType == Constants.FILTER_MAIN_RESULT_OPTION) {
                val dialogViewBinding = FilterMainResultDialogBinding.inflate(layoutInflater)
                setFilterSpinner(dialogViewBinding)
                view = dialogViewBinding.root

            } else if (dialogType == Constants.SEARCH_AREA_OPTION) {
                val dialogViewBinding = SearchAreaDialogBinding.inflate(layoutInflater)
                setAreaSpinner(dialogViewBinding)
                view = dialogViewBinding.root
                editText = dialogViewBinding.areaEditText

            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(
                    R.string.search
                ) { _, _ ->
                    if (dialogType == Constants.SEARCH_AREA_OPTION) {
                        try {
                            val d = editText.text.toString().toDouble()
                            type = typeSpinner.selectedItem.toString()
                            listener.onDialogPositiveClick(dialogType, d * 1000, city, type)
                        } catch (e: NumberFormatException) {
                            val d = 5.0
                            listener.onDialogPositiveClick(dialogType, -1.0, "غزة", "مطعم")
                        }

                    } else if (dialogType == Constants.FILTER_MAIN_RESULT_OPTION) {
                        city = citySpinner.selectedItem.toString()
                        type = typeSpinner.selectedItem.toString()
                        listener.onDialogPositiveClick(dialogType, -1.0, city, type)
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

    private fun setAreaSpinner(
        dialogViewBinding: SearchAreaDialogBinding
    ) {

        this.typeSpinner = dialogViewBinding.spinner

        val types = arrayOf(
            getString(R.string.restaurant),
            getString(R.string.coffee_shop),
            getString(R.string.coffee),
            getString(R.string.ice_cream),
            getString(R.string.sweets),
        )


        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, types)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = arrayAdapter

    }


    private fun setFilterSpinner(
        dialogViewBinding: FilterMainResultDialogBinding
    ) {

        this.typeSpinner = dialogViewBinding.typeSpinner
        this.citySpinner = dialogViewBinding.citySpinner

        val types = arrayOf(
            getString(R.string.restaurant),
            getString(R.string.coffee_shop),
            getString(R.string.coffee),
            getString(R.string.ice_cream),
            getString(R.string.sweets),
        )
        val cities = arrayOf(
            getString(R.string.gaza),
            getString(R.string.north),
            getString(R.string.kahan_yunis),
            getString(R.string.central),
            getString(R.string.rafah),
        )


        val typeAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        val cityAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, cities)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

    }
}

interface NoticeDialogListener {
    fun onDialogPositiveClick(dialogType: Int, distance: Double, city: String, type: String)
    fun onDialogNegativeClick(dialog: DialogFragment)
}
