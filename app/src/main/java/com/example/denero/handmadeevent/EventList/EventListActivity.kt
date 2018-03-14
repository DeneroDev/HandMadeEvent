package com.example.denero.handmadeevent.EventList

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.denero.handmadeevent.Event
import com.example.denero.handmadeevent.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.android.synthetic.main.activity_my_created_event_list.*

class EventListActivity : AppCompatActivity() {

    val TAG = "EventListActivity"

    lateinit var eventListAdapter : EventListAdapter
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var eventsDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        var eventList = arrayListOf<Event>()
        eventListAdapter = EventListAdapter(eventList)

        event_list_rec_view.layoutManager = LinearLayoutManager(applicationContext)
        event_list_rec_view.adapter = eventListAdapter
        event_list_rec_view.adapter.notifyDataSetChanged()

        firebaseDatabase = FirebaseDatabase.getInstance()
        eventsDatabaseReference = firebaseDatabase.reference.child("Events")

        eventsDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children){
                    val event = singleSnapshot.getValue(Event::class.java) as Event
                    eventListAdapter.eventList.add(event)
                    event_list_rec_view.adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

    }
}
