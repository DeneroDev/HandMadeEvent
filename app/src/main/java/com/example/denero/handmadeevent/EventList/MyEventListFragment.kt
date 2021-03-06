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
import kotlinx.android.synthetic.main.fragment_my_event_list.*
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyEventListFragment.OnMyEventListFragmentListener] interface
 * to handle interaction events.
 */
class MyEventListFragment : Fragment()
        , MyEventAdapter.onMyEventAdapterListener {

    override fun getIdSelectedEventForRemove(selectEventId: String) {
        removeEvent(selectEventId)

        removeRecordAttendees(selectEventId)
    }

    private fun removeEvent(selectEventId: String) {
        val myRef = FirebaseDatabase.getInstance().reference
                .child(NAME_TABLE_EVENT_DB
                        + getString(R.string.tag_separate_query_db)
                        + selectEventId).removeValue()
    }

    private fun removeRecordAttendees(selectEventId: String) {
        if (selectEventId!="") {
            val refAttendees = FirebaseDatabase.getInstance().reference
            pushLog("removeRecordAttendees", selectEventId)
            refAttendees.child(NAME_TABLE_ATTENDEES_EVENT_DB)
                    .orderByChild(selectEventId)
                    .equalTo(selectEventId)
                    .addChildEventListener(object : ChildEventListener {
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


                            FirebaseDatabase.getInstance().reference
                                    .child(NAME_TABLE_ATTENDEES_EVENT_DB
                                            + getString(R.string.tag_separate_query_db)
                                            + snapshot!!.key
                                            + getString(R.string.tag_separate_query_db)
                                            + selectEventId).removeValue()

                        }

                        override fun onChildRemoved(snapshot: DataSnapshot?) {
                            pushLog("onChildRemoved", snapshot.toString())
                        }

                    })
        }
    }

    override fun getIdSelectedEvent(selectEventId: String) {
       mListenerMyEventList!!.getEventSelectedId(selectEventId)
    }

    private var mListenerMyEventList: OnMyEventListFragmentListener? = null

    private var NUMBER_RECORDS_EVENT_FOR_SAMPLING: Int = -1
    private lateinit var NAME_TABLE_ATTENDEES_EVENT_DB: String
    private lateinit var NAME_TABLE_EVENT_DB: String

    private var adapter: MyEventAdapter? = null
    private val LOG_TAG = "GOT"
    private val LOG_HEAD = MyEventListFragment::class.java.simpleName
    private var eventList: MutableList<HashMap<String, Event>>? = null

    interface OnMyEventListFragmentListener {

        fun getEventSelectedId(idEventSelected: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnMyEventListFragmentListener) {
            mListenerMyEventList = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMyEventListFragmentListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventList = mutableListOf(hashMapOf())



        this.NUMBER_RECORDS_EVENT_FOR_SAMPLING = activity!!.resources.getInteger(R.integer.number_event_records_for_sample)
        this.NAME_TABLE_ATTENDEES_EVENT_DB = activity!!.getString(R.string.name_table_attendees_event_db)
        this.NAME_TABLE_EVENT_DB = activity!!.getString(R.string.name_table_event_db)

        recycler_list.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = MyEventAdapter(this, mutableListOf<HashMap<String, Event>>()
                , activity!!.applicationContext
                , activity!!.applicationContext.resources.getStringArray(R.array.month))
        recycler_list.adapter = adapter

        val myRef = FirebaseDatabase.getInstance().reference

        myRef.child(NAME_TABLE_EVENT_DB)
                .limitToFirst(NUMBER_RECORDS_EVENT_FOR_SAMPLING)
                .orderByChild("userCreated")
                .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)
                .addChildEventListener(object : ChildEventListener {
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
                        pushLog("onChildAdded", snapshot.toString())
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot?) {
                        for (event in eventList!!) {
                            event.remove(snapshot!!.key)
                            break
                        }
                        adapter!!.updateDate(eventList)
                    }

                })

        myRef.child(NAME_TABLE_EVENT_DB)
                .limitToFirst(NUMBER_RECORDS_EVENT_FOR_SAMPLING)
                .orderByChild("userCreated")
                .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(snapshot: DatabaseError?) {
                        pushLog("onCancelled", snapshot.toString())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        pushLog("my event", dataSnapshot.toString())
                        eventList?.clear()
                        for (singleSnapshot in dataSnapshot.children) {
                            eventList!!.add(hashMapOf(singleSnapshot.key to singleSnapshot.getValue(Event::class.java) as Event))
                        }
                        adapter!!.updateDate(eventList)

                    }
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_event_list, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        mListenerMyEventList = null
    }

    private fun pushLog(topic: String, message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }

}// Required empty public constructor
