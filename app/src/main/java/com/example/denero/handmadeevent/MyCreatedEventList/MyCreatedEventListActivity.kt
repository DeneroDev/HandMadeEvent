package com.example.denero.handmadeevent.MyCreatedEventList

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.denero.handmadeevent.Event
import com.example.denero.handmadeevent.EventList.EventListAdapter
import com.example.denero.handmadeevent.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.android.synthetic.main.activity_my_created_event_list.*

class MyCreatedEventListActivity : AppCompatActivity() {

    lateinit var eventListAdapter : MyCreatedEventListAdapter
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var eventsDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_created_event_list)

        var eventList = arrayListOf<Event>()
        eventListAdapter = MyCreatedEventListAdapter(eventList)

        my_created_event_list_rec_view.layoutManager = LinearLayoutManager(applicationContext)
        my_created_event_list_rec_view.adapter = eventListAdapter
        my_created_event_list_rec_view.adapter.notifyDataSetChanged()

        firebaseDatabase = FirebaseDatabase.getInstance()
        eventsDatabaseReference = firebaseDatabase.reference.child("Events")

        var userEmail = FirebaseAuth.getInstance().currentUser!!.email

        eventsDatabaseReference.orderByChild("userCreated").equalTo(userEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (singleSnapshot in dataSnapshot.children){
                            val event = singleSnapshot.getValue(Event::class.java) as Event
                            eventListAdapter.eventList.add(event)
                            my_created_event_list_rec_view.adapter.notifyDataSetChanged()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
    }
}
