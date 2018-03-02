package com.example.denero.handmadeevent

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import java.util.*
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Created by goga747 on 01.03.2018.
 */
class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var mListener: onDateFragmentListener? = null
    private lateinit var mission: String

    interface onDateFragmentListener {
        fun getDate(mission: String, date: String)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is onDateFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement onDateFragmentListener")
        }
    }
    private val LOG_TAG: String = "GOT"
    private val LOG_HEAD: String = DatePicker::class.java.simpleName
    private fun pushLog(topic: String, message: Any) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.arguments!!.containsKey(getString(R.string.key_bundle_missions_pickers))){

            mission = this.arguments!!.getString(getString(R.string.key_bundle_missions_pickers))
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(activity, this,
                year, month, day)
        picker.setTitle(getString(R.string.text_btn_data_picker))

        return picker
    }
    override fun onStart() {
        super.onStart()
        val nButton = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
        nButton.text = getString(R.string.text_btn_data_picker)

    }

    override fun onDateSet(datePicker: android.widget.DatePicker, year: Int,
                           month: Int, day: Int) {
        mListener!!.getDate(mission,"$year:$month:$day")

    }
}