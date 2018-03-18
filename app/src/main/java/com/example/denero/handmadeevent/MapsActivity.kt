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
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.denero.handmadeevent.EventList.DisplayFullEventFragment
import com.example.denero.handmadeevent.EventList.EventListActivity
import com.example.denero.handmadeevent.model.Event
import com.google.android.gms.maps.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_created_new_event.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MapsActivity() : AppCompatActivity(),
        OnMapReadyCallback,GoogleMap.OnMarkerDragListener,GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMapClickListener, Parcelable,ClusterManager.OnClusterClickListener<Event>, ClusterManager.OnClusterInfoWindowClickListener<Event>, ClusterManager.OnClusterItemClickListener<Event>, ClusterManager.OnClusterItemInfoWindowClickListener<Event> {

    override fun onClusterInfoWindowClick(p0: Cluster<Event>?) {

    }

    override fun onClusterItemInfoWindowClick(p0: Event?) {

    }

    override fun onMapClick(p0: LatLng?) {
//        mMarkerA.position = p0
    }

    override fun onCameraIdle() {
//        Toast.makeText(applicationContext,mMarkerA!!.position.toString(),Toast.LENGTH_LONG).show()
    }
//ffffffffffff

    lateinit var mMap: GoogleMap
    lateinit var lm:LocationManager
    lateinit var currentLocation:Location
    lateinit var mClusterManager:ClusterManager<Event>;
    lateinit var mAuth:FirebaseAuth
    lateinit var myRef:DatabaseReference
    lateinit var markers:List<String>
    lateinit var sp:SharedPreferences


    var locationListener = object:LocationListener{
        override fun onLocationChanged(location: Location?) {
            if (location != null) {
                if(sp.getBoolean("create",true)){
                    showLocation(location)

                }

                currentLocation = location
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {
            if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
            showLocation(lm.getLastKnownLocation(provider))}
            else
                ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
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
        sp = getSharedPreferences("eventimage", Context.MODE_PRIVATE)
        var e = sp.edit()
        e.putBoolean("create",true)
        e.apply()
        e.commit()


///DATABASE\\\\
//        myRef = FirebaseDatabase.getInstance().reference
        var query:Query = myRef
        query.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                var event: Event = p0!!.getValue(Event::class.java)!!
                event.pathDisk = sp.getString(event.titleEvent,"")
                Log.d("PROGRESS",sp.getString(event.titleEvent,""))
                try {
                    if(event.pathDisk==""){
                        Picasso.with(applicationContext)
                                .load(event.uriImage)
                                .resize(25,25)
                                .into(object :Target{
                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                        Log.d("PROGRESS","START")
                                    }

                                    override fun onBitmapFailed(errorDrawable: Drawable?) {
                                        Log.d("PROGRESS","FAIL")
                                    }

                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                        Log.d("PROGRESS","COMPLETE")
                                        // падает
                                        var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), event.titleEvent+".png")
                                            var fos = FileOutputStream(file)
                                        bitmap!!.compress(Bitmap.CompressFormat.JPEG,50,fos)
                                        event.pathDisk = file.absolutePath
                                        fos.close()
                                        Log.d("PROGRESS","COMPLETE_WRITE")
                                        var e = sp.edit()
                                        e.putString(event.titleEvent,file.absolutePath)
                                        e.apply()
                                        e.commit()
                                        event.pathDisk = file.absolutePath
                                        mClusterManager.addItem(event)
                                    }

                                })
                    }
                    else
                        mClusterManager.addItem(event)

                }catch (e:Exception){
                    Log.d("EXCEP_1",e.toString() + "\n" + event.uriImage + " End")
                    mClusterManager.addItem(event)
                }


            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }
        })


        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.action_add_event -> {startActivity(Intent(applicationContext,CreatedNewEventActivity::class.java))}
                R.id.action_all_event -> {startActivity(Intent(applicationContext,EventListActivity::class.java))}
                R.id.action_open_favorites -> {startActivity(Intent(applicationContext
                        ,EventListActivity::class.java)
                        .putExtra(getString(R.string.key_mission_open_fragment)
                                ,getString(R.string.key_signed_event_fragment)))}
            }
            false
        }



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

    override fun onClusterClick(p0: Cluster<Event>): Boolean {

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        val builder = LatLngBounds.builder()
        for (item in p0.getItems()) {
            builder.include(item.getPosition())
        }
        // Get the LatLngBounds
        val bounds = builder.build()

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return true
    }



    override fun onClusterItemClick(p0: Event?): Boolean {
       var intent = Intent(applicationContext,FullEventMapActivity::class.java)
        intent.putExtra("pathImage",p0!!.pathDisk)
        intent.putExtra("eventDesctiption",p0.description)
        intent.putExtra("eventTitle",p0.titleEvent)
        intent.putExtra("eventURL",p0.uriImage)
        intent.putExtra("eventCreator",p0.userCreated)
        intent.putExtra("eventLongitude",p0.longitude)
        intent.putExtra("eventLatitude",p0.latitude)
        intent.putExtra("eventTimeStart",p0.dateStart)
        intent.putExtra("eventTimeExpiration",p0.dateExpiration)
        intent.putExtra("eventTimeCreate", p0.createdTimeInMillis)
        Log.d("LONGI",p0.longitude.toString())
        Log.d("LATITUDE",p0.latitude.toString())
        Log.d("NAME",p0.titleEvent)
        startActivity(intent)

        return true
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
        mClusterManager = ClusterManager<Event>(this, mMap)
        mClusterManager.setRenderer(EventRenderer(applicationContext,mMap,mClusterManager,layoutInflater,resources))
        mMap.setOnCameraIdleListener(mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)
        mMap.setOnInfoWindowClickListener(mClusterManager)
        mClusterManager.setOnClusterClickListener(this)
        mClusterManager.setOnClusterInfoWindowClickListener(this)
        mClusterManager.setOnClusterItemClickListener(this)
        mClusterManager.setOnClusterItemInfoWindowClickListener(this)
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
                .zoom(16f)
                .build()
        var cameraUpdate =  CameraUpdateFactory.newCameraPosition(cameraPosition)
        if(sp.getBoolean("create",true)) {
            var e = sp.edit()
            e.putBoolean("create", false)
            e.apply()
            e.commit()
            mMap.animateCamera(cameraUpdate)
        }
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


    class EventRenderer(var applicationContext:Context,var mMap:GoogleMap,var mClusterManager: ClusterManager<Event>
                        ,var layoutInflater: LayoutInflater,var resources: Resources):DefaultClusterRenderer<Event>(applicationContext,mMap,mClusterManager){
        private var mIconGenerator = IconGenerator(applicationContext)
        private var mClusterIconGenerator = IconGenerator(applicationContext)
        private lateinit var mImageView: ImageView
        private lateinit var mClusterImageView: ImageView
        private var mDimension: Int = 0
        init {
            var multiProfile = layoutInflater.inflate(R.layout.multi_profile, null)
            mClusterIconGenerator.setContentView(multiProfile)
            mClusterImageView = multiProfile.findViewById(R.id.image)

            mImageView = ImageView(applicationContext)
            mDimension = 100
            mImageView.layoutParams = ViewGroup.LayoutParams(mDimension, mDimension)
            var padding = 2
            mImageView.setPadding(padding, padding, padding, padding)
            mIconGenerator.setContentView(mImageView)
        }

        override fun onBeforeClusterItemRendered(item: Event, markerOptions: MarkerOptions?) {
        try{
            Log.d("BEFORE:",item.pathDisk)
            var bitmap = BitmapFactory.decodeFile(item.pathDisk)
            mImageView.setImageBitmap(bitmap)
            val icon = mIconGenerator.makeIcon()
            markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .title(item.titleEvent)
                    .snippet(item.description)
           }
         catch (e:Exception){
                Log.d("EXCEP_2",e.toString())
                mImageView.setImageBitmap(BitmapFactory.decodeResource(resources,R.drawable.camera))
                val icon = mIconGenerator.makeIcon()
                markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .title(item.titleEvent)
                        .snippet(item.description)
            }

        }

        override fun onBeforeClusterRendered(cluster: Cluster<Event>?, markerOptions: MarkerOptions?) {
            try{
                val profilePhotos = ArrayList<Drawable>(Math.min(4, cluster!!.getSize()))
                val width = mDimension
                val height = mDimension

                for (p in cluster!!.getItems()) {
                    // Draw 4 at most.
                    if (profilePhotos.size == 4) break
                    val drawable = mImageView.drawable
                    drawable.setBounds(0, 0, width, height)
                    profilePhotos.add(drawable)
                }
                val multiDrawable = MultiDrawable(profilePhotos)
                multiDrawable.setBounds(0, 0, width, height)

                mClusterImageView.setImageDrawable(multiDrawable)
                val icon = mClusterIconGenerator.makeIcon(cluster.getSize().toString())
                markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(icon))
            }catch (e:Exception){
                Toast.makeText(applicationContext,"Что-то не так!",Toast.LENGTH_LONG).show()
            }

        }

        override fun shouldRenderAsCluster(cluster: Cluster<Event>?): Boolean {
            return cluster!!.getSize() > 1
        }

    }
}


