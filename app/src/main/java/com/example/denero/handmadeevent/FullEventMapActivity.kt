package com.example.denero.handmadeevent

import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.example.denero.handmadeevent.Notification.NotificationSubscription
import com.example.denero.handmadeevent.model.Event
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_maps_event.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Denero on 16.03.2018.
 */
class FullEventMapActivity: AppCompatActivity()
, OnMapReadyCallback {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_maps_event)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        toolbar_layout.contentScrim = ContextCompat.getDrawable(applicationContext,R.drawable.toolbar_collaps)
        uploadImageForTitle()
        displayMap()
        title_map_event.setText(intent.getStringExtra("eventTitle"))
        title_map_creator.setText("Event create:"+parseDatatoString(intent.getLongExtra("eventTimeCreate",0)))
        title_map_description.setText(intent.getStringExtra("eventDesctiption"))
        title_map_start.setText("Event started:"+parseDatatoString(intent.getLongExtra("eventTimeStart",0)))
        title_map_end.setText("Event ended:"+parseDatatoString(intent.getLongExtra("eventTimeExpiration",0)))

        subscr_event.setOnClickListener {
            val myRef = FirebaseDatabase.getInstance().reference.child(getString(R.string.name_table_attendees_event_db) + getString(R.string.tag_separate_query_db) + FirebaseAuth.getInstance().currentUser!!.uid)
            myRef.child(intent.getStringExtra("eventId")).setValue(intent.getStringExtra("eventId"))
            NotificationSubscription().subscribeOn(Event(intent.getStringExtra("eventCreator"),"","",0.0,0.0,intent.getLongExtra("eventTimeStart",0),0,intent.getLongExtra("eventTimeCreate",0)))
            subscr_event.visibility  = View.GONE
        }



        btn_back.setOnClickListener {
            finish()
        }
    }

    private fun uploadImageForTitle(){
        try{ Picasso.with(applicationContext)
                .load(intent.getStringExtra("eventURL"))
                .placeholder(R.mipmap.camera_colored)
                .fit()
                .centerCrop()
                .into(main_backdrop_activity)}
        catch(e:Exception){
            main_backdrop_activity.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra("pathImage")))
        }
    }

    private fun parseDatatoString(date:Long):String{
        var d = Date(date)
        return d.hours.toString() +":"+d.minutes+":"+d.seconds+"("+dayToWeak(d.day)+")" + "|" + d.date +"/"+d.month+"/"+d.year
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
        var longitude = intent.getDoubleExtra("eventLongitude",0.0)
        var latitude = intent.getDoubleExtra("eventLatitude",0.0)
        Log.d("LONGI",longitude.toString())
        Log.d("LATITUDE",latitude.toString())
        p0!!.addMarker(MarkerOptions().position(LatLng(longitude,latitude)))
        var cameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude,longitude))
                .build()
        var cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        p0.animateCamera(cameraUpdate)
    }


}