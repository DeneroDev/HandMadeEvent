package com.example.denero.handmadeevent

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import java.util.*


/**
 * Created by goga747 on 02.03.2018.
 */
class TimePicker : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var mListener: onTimeFragmentListener? = null
    private lateinit var mission: String

    interface onTimeFragmentListener {
        fun getTime(mission: String, time: MutableMap<String, String>)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is onTimeFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement onTimeFragmentListener")
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
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val picker = TimePickerDialog(activity, this, hour, minute, true)
        picker.setTitle(getString(R.string.text_btn_time_picker))

        return picker
    }

    override fun onStart() {
        super.onStart()
        val nButton = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
        nButton.text = getString(R.string.text_btn_time_picker)

    }

    override fun onTimeSet(view: TimePicker, hours: Int, minute: Int) {
        var timeMap = mutableMapOf<String,String>()
        timeMap.put(getString(R.string.key_hours_map_date),hours.toString())
        timeMap.put(getString(R.string.key_minute_map_date),minute.toString())
        mListener!!.getTime(mission,timeMap)
    }

}