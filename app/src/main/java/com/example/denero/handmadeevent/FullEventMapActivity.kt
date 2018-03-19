package com.example.denero.handmadeevent

import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.denero.handmadeevent.Notification.NotificationSubscription
import com.example.denero.handmadeevent.model.Event
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_maps_event.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Denero on 16.03.2018.
 */
class FullEventMapActivity: AppCompatActivity()
, OnMapReadyCallback {

    private lateinit var event:Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_maps_event)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        toolbar_layout.contentScrim = ContextCompat.getDrawable(applicationContext,R.drawable.toolbar_collaps)

        displayMap()

        val myRef = FirebaseDatabase.getInstance().getReference("Events")
        myRef.child(intent.getStringExtra("eventId")).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                event = p0!!.getValue(Event::class.java)!!

                title_map_event.setText(event.titleEvent)
                title_map_creator.setText("Event create:"+parseDatatoString(event.createdTimeInMillis))
                title_map_description.setText(event.description)
                title_map_start.setText("Event started:"+parseDatatoString(event.dateStart))
                title_map_end.setText("Event ended:"+parseDatatoString(event.dateExpiration))
                uploadImageForTitle()


            }
        })

        subscr_event.setOnClickListener {
            val myRef = FirebaseDatabase.getInstance().reference.child(getString(R.string.name_table_attendees_event_db) + getString(R.string.tag_separate_query_db) + FirebaseAuth.getInstance().currentUser!!.uid)
            myRef.child(intent.getStringExtra("eventId")).setValue(intent.getStringExtra("eventId"))
            NotificationSubscription().subscribeOn(Event(event.userCreated,"","",0.0,0.0,event.dateStart,0,event.createdTimeInMillis))
            subscr_event.visibility  = View.GONE
        }



        btn_back.setOnClickListener {
            finish()
        }
    }

    private fun uploadImageForTitle(){
        try{ Picasso.with(applicationContext)
                .load(event.uriImage)
                .placeholder(R.mipmap.camera_colored)
                .fit()
                .centerCrop()
                .into(main_backdrop_activity)}
        catch(e:Exception){
            Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
        }
    }

    private fun parseDatatoString(date:Long):String{
        var d = Date(date)
        val monthArrays =  applicationContext.resources.getStringArray(R.array.month)
        return d.hours.toString() +":"+d.minutes+":"+d.seconds+"("+dayToWeak(d.day)+")" + "|" + d.date +"  "+monthArrays[d.month-1]
    }

    private fun dayToWeak(day:Int):String{
        when(day){
            1 -> return getString(R.string.weak_day_1)
            2 -> return getString(R.string.weak_day_2)
            3 -> return getString(R.string.weak_day_3)
            4 -> return getString(R.string.weak_day_4)
            5 -> return getString(R.string.weak_day_5)
            6 -> return getString(R.string.weak_day_6)
            7 -> return getString(R.string.weak_day_7)
        }
        return ""
    }

    private fun displayMap() {
        val options = GoogleMapOptions()
        options.compassEnabled(true).zoomControlsEnabled(true)

        val mMapFragment = SupportMapFragment.newInstance(options)
        mMapFragment.getMapAsync(this)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.google_maps_fragment, mMapFragment, getString(R.string.tag_maps_fragment))
        fragmentTransaction.commit()
    }

    override fun onMapReady(p0: GoogleMap?) {
        var cameraPosition:CameraPosition
        var map = p0
        cameraPosition = CameraPosition.Builder()
                .target(LatLng(event.latitude,event.longitude))
                .zoom(18f)
                .build()
        map!!.addMarker(MarkerOptions().position(LatLng(event.latitude,event.longitude)))
        var cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        map!!.animateCamera(cameraUpdate)

    }


}