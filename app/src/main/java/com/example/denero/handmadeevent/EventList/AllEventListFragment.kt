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
import kotlinx.android.synthetic.main.fragment_all_event_list.*
import java.io.Serializable


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

    override fun onCancelled(p0: DatabaseError?) {

    }

    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

    }

    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

    }

    override fun onChildAdded(snapshot: DataSnapshot?, p1: String?) {
        eventList!!.add(hashMapOf(snapshot!!.key to snapshot.getValue(Event::class.java) as Event))
        adapter!!.updateDate(eventList)
    }

    override fun onChildRemoved(snapshot: DataSnapshot?) {
        for (event in this!!.eventList!!) {
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

        this.NUMBER_EVENT_RECORDS_FOR_SAMPLE = activity!!.resources.getInteger(R.integer.number_event_records_for_sample)
        this.NAME_TABLE_EVENT_DB = activity!!.getString(R.string.name_table_event_db)

        recycler_list.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = AllEventAdapter(this, mutableListOf(hashMapOf()))
        recycler_list.adapter = adapter

        val myRef = FirebaseDatabase.getInstance().reference
//TODO: выводит не все записи!!!
        myRef.child(NAME_TABLE_EVENT_DB).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMPLE).addChildEventListener(this)

        myRef.child(NAME_TABLE_EVENT_DB).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMPLE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
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
