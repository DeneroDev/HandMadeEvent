package com.example.denero.handmadeevent.EventList

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.denero.handmadeevent.R
import com.example.denero.handmadeevent.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_all_event_list.*
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignedEventFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class SignedEventFragment : Fragment()
        , SignedEventAdapter.onSignedEventAdapterListener {

    override fun getIdSelectedEvent(selectEventId: String) {

        mListener!!.getEventSelectedId(selectEventId)
    }

    override fun getIdSelectedEventForUnsubscribe(selectEventId: String) {
        val myRef = FirebaseDatabase.getInstance().reference
                .child(getString(R.string.name_table_attendees_event_db)
                        + getString(R.string.tag_separate_query_db)
                        + FirebaseAuth.getInstance().currentUser!!.uid
                        + getString(R.string.tag_separate_query_db)
                        + selectEventId).removeValue()
    }

    private var mListener: OnFragmentInteractionListener? = null

    private var NUMBER_EVENT_RECORDS_FOR_SAMLE: Int = -1
    private lateinit var NAME_TABLE_ATTENDEES_EVENT_DB: String
    private lateinit var NAME_TABLE_EVENT_DB: String

    private val LOG_TAG = "GOT"
    private val LOG_HEAD = SignedEventFragment::class.java.simpleName
    private var eventList: MutableList<HashMap<String, Event>>? = null
    private var attendeesEventList: MutableList<String>? = null
    private var adapter: SignedEventAdapter? = null

    interface OnFragmentInteractionListener {
        fun closeMe(keyFragment: String)
        fun getEventSelectedId(idEventSelected: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventList = mutableListOf(hashMapOf())
        attendeesEventList = mutableListOf()

        this.NUMBER_EVENT_RECORDS_FOR_SAMLE = activity!!.resources.getInteger(R.integer.number_event_records_for_sample)
        this.NAME_TABLE_ATTENDEES_EVENT_DB = activity!!.getString(R.string.name_table_attendees_event_db)
        this.NAME_TABLE_EVENT_DB = activity!!.getString(R.string.name_table_event_db)

        recycler_list.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = SignedEventAdapter(this, mutableListOf(hashMapOf()))
        recycler_list.adapter = adapter

        val myRef = FirebaseDatabase.getInstance().getReference(NAME_TABLE_ATTENDEES_EVENT_DB)

        myRef.child(FirebaseAuth.getInstance().currentUser!!.uid).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMLE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                attendeesEventList?.clear()
                if (dataSnapshot.value == null) {
                    mListener!!.closeMe(activity!!.getString(R.string.key_signed_events_fragment)) // падает
                } else {

                    for (idEvent in dataSnapshot.children) {
//
                        attendeesEventList!!.add(idEvent.key)
                    }


                    completeData()
                }


            }
        })

    }

    private fun completeData() {
        //TODO: ИДИОТИЗМ
        val myRef = FirebaseDatabase.getInstance().reference
        myRef.child(NAME_TABLE_EVENT_DB).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMLE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventList?.clear()

                for (singleSnapshot in dataSnapshot.children) {
//
                    if (attendeesEventList!!.any { it == singleSnapshot.key }) {

                        eventList!!.add(hashMapOf(singleSnapshot.key to singleSnapshot.getValue(Event::class.java) as Event))
                    } else {
                        continue
                    }


                }

                adapter!!.updateDate(eventList)

            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signed_event, container, false)
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun pushLog(topic: String, message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}// Required empty public constructor
