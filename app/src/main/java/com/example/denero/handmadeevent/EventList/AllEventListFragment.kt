package com.example.denero.handmadeevent.EventList

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.denero.handmadeevent.R
import com.example.denero.handmadeevent.model.Event
import com.google.firebase.database.*
import java.io.Serializable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_all_event_list.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AllEventListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class AllEventListFragment : Fragment()
        , ChildEventListener
        , AllEventAdapter.onAllEventAdapterListener{
    override fun getEvent(selectEvent: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCancelled(p0: DatabaseError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onChildAdded(snapshot: DataSnapshot?, p1: String?) {



    }

    override fun onChildRemoved(p0: DataSnapshot?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val LOG_TAG = "GOT"
    private val LOG_HEAD = AllEventListFragment::class.java.simpleName
    private val eventMap: MutableMap<String, Event>? = null
    private val NUMBER_EVENT_RECORDS_FOR_SAMLE: Int = resources.getInteger(R.integer.number_event_records_for_sample)
    private var adapter:AllEventAdapter? = null
    private var mListener: OnFragmentInteractionListener? = null

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    lateinit var myRef: DatabaseReference
    fun s() {
        var query: Query = myRef.child("Events").limitToLast(4)
        query.addChildEventListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_list.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = AllEventAdapter(this,null)
        recycler_list.adapter = adapter

        myRef = FirebaseDatabase.getInstance().reference

        myRef.child(getString(R.string.name_table_event_db)).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMLE).addChildEventListener(this)

        myRef.child(getString(R.string.name_table_event_db)).limitToFirst(NUMBER_EVENT_RECORDS_FOR_SAMLE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventMap!!.clear()
                for (singleSnapshot in dataSnapshot.children) {
                    pushLog("onDataChange singleSnapshot", singleSnapshot.toString())
                    eventMap.put(singleSnapshot.key, singleSnapshot.getValue(Event::class.java) as Event)

                }

            }
        })

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_event_list, container, false)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
