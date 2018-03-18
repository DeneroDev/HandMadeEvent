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
import com.google.firebase.database.*
import java.io.Serializable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_all_event_list.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AllEventListFragment.OnAllEventFragmentListener] interface
 * to handle interaction events.
 */
class AllEventListFragment : Fragment()
        , ChildEventListener
        , AllEventAdapter.onAllEventAdapterListener {
    override fun getIdSelectedEventForSubscribe(selectEventId: String) {
        subscribeForEvent(selectEventId)
    }

    private fun subscribeForEvent(selectEventId: String) {
        //TODO: сообщить челу о подписки

        val myRef = FirebaseDatabase.getInstance().reference.child(getString(R.string.name_table_attendees_event_db) + getString(R.string.tag_separate_query_db) + FirebaseAuth.getInstance().currentUser!!.uid)
        myRef.child(selectEventId).setValue(selectEventId)

    }

    override fun getIdSelectedEvent(selectEventId: String) {
        mListenerAllEvent!!.getEventSelectedId(selectEventId)
    }

    override fun onCancelled(snapshot: DatabaseError?) {
        pushLog("onCancelled", snapshot.toString())
    }

    override fun onChildMoved(snapshot: DataSnapshot?, p1: String?) {
        pushLog("onChildMoved", snapshot.toString())
    }

    override fun onChildChanged(snapshot: DataSnapshot?, p1: String?) {
        pushLog("onChildChanged", snapshot.toString())
    }

    override fun onChildAdded(snapshot: DataSnapshot?, p1: String?) {
        eventList!!.add(hashMapOf(snapshot!!.key to snapshot.getValue(Event::class.java) as Event))
        adapter!!.updateDate(eventList)
    }

    override fun onChildRemoved(snapshot: DataSnapshot?) {
        for (event in this.eventList!!) {
            event.remove(snapshot!!.key)
            break
        }
        adapter!!.updateDate(eventList)
    }

    private var NUMBER_EVENT_RECORDS_FOR_SAMPLE: Int = -1
    private lateinit var NAME_TABLE_EVENT_DB: String

    private val LOG_TAG = "GOT"
    private val LOG_HEAD = AllEventListFragment::class.java.simpleName
    private var eventList: MutableList<HashMap<String, Event>>? = null
    private var attendeesEventList: MutableList<String>? = null
    private var adapter: AllEventAdapter? = null
    private var mListenerAllEvent: OnAllEventFragmentListener? = null


    interface OnAllEventFragmentListener {
        fun getEventSelectedId(idEventSelected: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnAllEventFragmentListener) {
            mListenerAllEvent = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnAllEventFragmentListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventList = mutableListOf(hashMapOf())
        attendeesEventList = mutableListOf()

        this.NUMBER_EVENT_RECORDS_FOR_SAMPLE = activity!!.resources.getInteger(R.integer.number_event_records_for_sample)
        this.NAME_TABLE_EVENT_DB = activity!!.getString(R.string.name_table_event_db)

        recycler_list.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = AllEventAdapter(this, mutableListOf<HashMap<String, Event>>()
                , activity!!.applicationContext
                , activity!!.applicationContext.resources.getStringArray(R.array.month))
        recycler_list.adapter = adapter

        FirebaseDatabase.getInstance().getReference(getString(R.string.name_table_attendees_event_db))
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(snapshot: DatabaseError?) {
                        pushLog("onCancelled", snapshot.toString())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        attendeesEventList?.clear()
                        pushLog("dataSnapshot.value 1", dataSnapshot!!.value.toString())
                        if (dataSnapshot!!.value != null) {
                            for (idEvent in dataSnapshot.children) {
                                attendeesEventList!!.add(idEvent.key)

                            }
                            completeDateAdapter()

                        }
                    }

                })


    }

    private fun completeDateAdapter() {
        val myRefEvent = FirebaseDatabase.getInstance().reference

        myRefEvent.child(NAME_TABLE_EVENT_DB).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMPLE).addChildEventListener(this)

        myRefEvent.child(NAME_TABLE_EVENT_DB).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMPLE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError?) {
                pushLog("onCancelled", snapshot.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventList?.clear()
                pushLog("onDataChange TUT", dataSnapshot.toString())
                pushLog("onDataChange attendes",  attendeesEventList.toString())
                for (singleSnapshot in dataSnapshot.children) {
                    val event = singleSnapshot.getValue(Event::class.java) as Event

                    if ((event.userCreated != FirebaseAuth.getInstance().currentUser!!.uid) ) //and
                        pushLog("1","1")
                        if (attendeesEventList!!.isNotEmpty()){
                            if ((attendeesEventList!!.none { it == singleSnapshot.key })){
                                pushLog("2","2")
                                eventList!!.add(hashMapOf(singleSnapshot.key to event))
                                continue
                            }
                        }else{
                            pushLog("3","3")
                            eventList!!.add(hashMapOf(singleSnapshot.key to event))
                        }

                }

                adapter!!.updateDate(eventList)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_all_event_list, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        mListenerAllEvent = null
    }

    private fun pushLog(topic: String, message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }

}// Required empty public constructor
