package com.example.denero.handmadeevent

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import com.example.denero.handmadeevent.EventList.EventListActivity
import com.example.denero.handmadeevent.model.Event
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_created_new_event.*
import java.security.Permission
import java.util.jar.Manifest


class MapsActivity() : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMarkerDragListener,GoogleMap.OnCameraIdleListener,GoogleMap.OnMapClickListener, Parcelable {
    override fun onMapClick(p0: LatLng?) {
        mMarkerA.position = p0
    }

    override fun onCameraIdle() {
//        Toast.makeText(applicationContext,mMarkerA!!.position.toString(),Toast.LENGTH_LONG).show()
    }
//ffffffffffff

    private lateinit var mMap: GoogleMap
    lateinit var mMarkerA:Marker
    lateinit var lm:LocationManager
    lateinit var currentLocation:Location

    lateinit var mAuth:FirebaseAuth
    lateinit var myRef:DatabaseReference
    lateinit var markers:List<String>


    var locationListener = object:LocationListener{
        override fun onLocationChanged(location: Location?) {
            if (location != null) {
                showLocation(location)
                currentLocation = location
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        @SuppressLint("MissingPermission")
        override fun onProviderEnabled(provider: String?) {
            showLocation(lm.getLastKnownLocation(provider))
        }

        override fun onProviderDisabled(provider: String?) {
           
        }

    }

    constructor(parcel: Parcel) : this() {
        currentLocation = parcel.readParcelable(Location::class.java.classLoader)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        FirebaseApp.initializeApp(applicationContext)
        mAuth = FirebaseAuth.getInstance()
        var database = FirebaseDatabase.getInstance()
        myRef = database.getReference(getString(R.string.name_table_event_db))
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

///DATABASE\\\\
//        myRef = FirebaseDatabase.getInstance().reference
        var query:Query = myRef
        query.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {

                var event: Event = p0!!.getValue(Event::class.java)!!
                mMap.addMarker(MarkerOptions()
                        .position(LatLng(event.latitude,event.longitude))
                        .title(event.titleEvent)
                        .snippet(event.description))

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }
        })


        bottom_navigation.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.action_add_event -> {startActivity(Intent(applicationContext,CreatedNewEventActivity::class.java))}
                    R.id.action_all_event -> {startActivity(Intent(applicationContext,EventListActivity::class.java))}
                    R.id.action_open_favorites -> {startActivity(Intent(applicationContext
                            ,EventListActivity::class.java)
                                .putExtra(getString(R.string.key_signed_events_fragment)
                                    ,getString(R.string.key_signed_events_fragment)))}
                }
                return false
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*10,10f,locationListener)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*10,10f,locationListener)
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerDragListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnMapClickListener(this)

        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarkerA.position, 18f))
        mMap.getUiSettings().setMyLocationButtonEnabled(true)
        if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true)
        }
        mMarkerA = mMap.addMarker(MarkerOptions().draggable(true)
                .position(LatLng(0.0,0.0)))
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        Toast.makeText(applicationContext,p0!!.position.toString(),Toast.LENGTH_LONG).show()
    }

    override fun onMarkerDragStart(p0: Marker?) {
        var cameraPosition = CameraPosition.Builder()
                .target(p0!!.position)
                .zoom(16.5f)
                .build()
        var cameraUpdate =  CameraUpdateFactory.newCameraPosition(cameraPosition)
        mMap.animateCamera(cameraUpdate)
    }

    override fun onMarkerDrag(p0: Marker?) {

    }

    fun showLocation(location: Location){
        if(location==null){
            return
        }
        if(location.provider.equals(LocationManager.GPS_PROVIDER)){
            SetLoc(location)
        }
        else{
            if(location.provider.equals(LocationManager.NETWORK_PROVIDER)){
                SetLoc(location)
            }
        }
    }

    fun SetLoc(location:Location){
        var cameraPosition = CameraPosition.Builder()
                .target(LatLng(location.latitude,location.longitude))
                .zoom(18f)
                .build()
        var cameraUpdate =  CameraUpdateFactory.newCameraPosition(cameraPosition)
        mMap.animateCamera(cameraUpdate)

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(currentLocation, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MapsActivity> {
        override fun createFromParcel(parcel: Parcel): MapsActivity {
            return MapsActivity(parcel)
        }

        override fun newArray(size: Int): Array<MapsActivity?> {
            return arrayOfNulls(size)
        }
    }


}
