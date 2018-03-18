package com.example.denero.handmadeevent.EventList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.denero.handmadeevent.R
import com.example.denero.handmadeevent.model.Event
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_display_full_event.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


class DisplayFullEventFragment : Fragment() {


    private var mListener: OnDisplayFullEventFragmentListener? = null
    private val LOG_TAG = "GOT"
    private val LOG_HEAD = DisplayFullEventFragment::class.java.simpleName
    private lateinit var NAME_TABLE_EVENT_DB: String
    private lateinit var idDisplayEvent: String


    interface OnDisplayFullEventFragmentListener {
        fun closeMe(keyFragment: String)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (this.arguments != null && this.arguments!!.containsKey(getString(R.string.key_id_event_selected))) {
            idDisplayEvent = this.arguments!!.getString(getString(R.string.key_id_event_selected))

        }
        return inflater.inflate(R.layout.fragment_display_full_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.NAME_TABLE_EVENT_DB = activity!!.getString(R.string.name_table_event_db)

        val myRef = FirebaseDatabase.getInstance().getReference(NAME_TABLE_EVENT_DB)

        myRef.child(idDisplayEvent).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError?) {
                pushLog("onCancelled", snapshot.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                if (snapshot!!.value == null) {
                    mListener!!.closeMe(getString(R.string.key_full_event_fragment))
                } else {
                    displayInfo(snapshot!!.getValue(Event::class.java) as Event)
                }

            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun displayInfo(event: Event) {
        tv_full_event_location.text = "${event.latitude} : ${event.longitude}"
        tv_full_event_title.text = event.titleEvent
        tv_full_event_description.text = event.description

        tv_full_event_date_start.text = buildDisplayTime(getTimeString(event.dateStart))
        tv_full_event_date_expiration.text = buildDisplayTime(getTimeString(event.dateExpiration))
        if (!event.uriImage.isEmpty()) {
            Picasso.with(context).load(event.uriImage)
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .error(R.mipmap.ic_launcher)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerCrop()
                    .into(image_full_event)

        }

    }

    private fun getTimeString(timeMillionsSecond: Long): String? {

        val date = Date(timeMillionsSecond)
        val formatter = SimpleDateFormat("M:d:HH:mm")
        return formatter.format(date)
    }

    private fun buildDisplayTime(timeString: String?): String {
        val fullDateExpirationString: String = timeString!!
        val tagSeparate = activity!!.getString(R.string.tag_separate_date)
        val separateFullDateExpirationList = fullDateExpirationString.split((tagSeparate).toRegex())
        val month = activity!!.applicationContext.resources.getStringArray(R.array.month)

        return month[separateFullDateExpirationList[0].toInt()] +
                tagSeparate +
                separateFullDateExpirationList[1] +
                tagSeparate +
                separateFullDateExpirationList[2] +
                tagSeparate +
                separateFullDateExpirationList[3]

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnDisplayFullEventFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnDisplayFullEventFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun pushLog(topic: String, message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }

}// Required empty public constructor
