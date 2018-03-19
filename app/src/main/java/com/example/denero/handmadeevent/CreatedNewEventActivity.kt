package com.example.denero.handmadeevent

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.denero.handmadeevent.EventList.EventListActivity
import com.example.denero.handmadeevent.Notification.RetrofitApiHelper
import com.example.denero.handmadeevent.model.Event
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickCancel
import com.vansuita.pickimage.listeners.IPickResult
import kotlinx.android.synthetic.main.activity_created_new_event.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import java.util.*

//TODO: CreatedNewEventActivity
/*
 сохранять ли сделанные фото?
*/
class CreatedNewEventActivity : AppCompatActivity(),
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        DatePicker.onDateFragmentListener,
        TimePicker.onTimeFragmentListener,
        IPickResult {


    private lateinit var myRef: DatabaseReference
    private var eventImageUri: Uri
    private val LOCATION_PERMISSION_REQUEST_ID: Int = 3
    private val LOG_TAG: String = "GOT"
    private val LOG_HEAD: String = CreatedNewEventActivity::class.java.simpleName
    private var mMap: GoogleMap? = null

    private var dateStartMap: MutableMap<String, String>
    private var dateExpirationMap: MutableMap<String, String>
    private var timeStartMap: MutableMap<String, String>
    private var timeExpirationMap: MutableMap<String, String>
    private var locationPoint: LatLng


    init {
        this.eventImageUri = Uri.EMPTY
        this.dateStartMap = mutableMapOf()
        this.dateExpirationMap = mutableMapOf()
        this.timeStartMap = mutableMapOf()
        this.timeExpirationMap = mutableMapOf()
        this.locationPoint = LatLng(0.0, 0.0)
    }

    override fun onMyLocationClick(p0: Location) {

    }

    private fun chooseTime(mission: String) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.key_bundle_missions_pickers), mission)
        val timeDialog = TimePicker()
        timeDialog.arguments = bundle
        timeDialog.show(supportFragmentManager, getString(R.string.tag_time_picker))
    }

    override fun getTime(mission: String, time: MutableMap<String, String>) {
        val timeDisplay = buildTimeFromDisplay(time)
        when (mission) {
            getString(R.string.key_mission_choose_time_start) -> {
                this.timeStartMap.clear()
                this.timeStartMap = time
            }
            getString(R.string.key_mission_choose_time_expiration) -> {
                this.timeExpirationMap.clear()
                this.timeExpirationMap = time
            }
        }
        setTimeDisplay(mission, timeDisplay)
    }

    private fun buildTimeFromDisplay(time: MutableMap<String, String>): String {
        val minuteString: String = if (time[getString(R.string.key_minute_map_date)]!!.toInt() >= 10) {
            time[getString(R.string.key_minute_map_date)].toString()
        } else {
            "0" + time[getString(R.string.key_minute_map_date)]
        }
        val hoursString: String = if (time[getString(R.string.key_hours_map_date)]!!.toInt() >= 10) {
            time[getString(R.string.key_hours_map_date)].toString()
        } else {
            "0" + time[getString(R.string.key_hours_map_date)]
        }
        return hoursString + getString(R.string.tag_separate_date) + minuteString
    }

    private fun setTimeDisplay(mission: String, time: String) {
        when (mission) {
            getString(R.string.key_mission_choose_time_start) -> tv_new_event_time_start.text = time
            getString(R.string.key_mission_choose_time_expiration) -> tv_new_event_time_expiration.text = time
        }
    }

    override fun getDate(mission: String, date: MutableMap<String, String>) {
        val dateString = buildDateFromDisplay(date)

        when (mission) {
            getString(R.string.key_mission_choose_date_start) -> {
                this.dateStartMap.clear()
                this.dateStartMap = date
            }
            getString(R.string.key_mission_choose_date_expiration) -> {
                this.dateExpirationMap.clear()
                this.dateExpirationMap = date
            }
        }
        setDateDisplay(mission, dateString)
    }

    private fun buildDateFromDisplay(date: MutableMap<String, String>): String {
        val monthArrays = resources.getStringArray(R.array.month)
        return "${date[getString(R.string.key_year_map_date)]}" +
                ":${monthArrays[date[getString(R.string.key_month_map_date)]!!.toInt()]}" +
                ":${date[getString(R.string.key_day_map_date)]}"
    }

    private fun setDateDisplay(mission: String, date: String) {
        when (mission) {
            getString(R.string.key_mission_choose_date_start) -> {
                tv_new_event_date_start.text = date
            }
            getString(R.string.key_mission_choose_date_expiration) -> {
                tv_new_event_date_expiration.text = date
            }
        }
    }

    private fun chooseDate(mission: String) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.key_bundle_missions_pickers), mission)
        val dateDialog = DatePicker()
        dateDialog.arguments = bundle
        dateDialog.show(supportFragmentManager, getString(R.string.tag_date_picker))
    }

    private fun chooseLocation() {
        displayMap()
    }

    override fun onMyLocationButtonClick(): Boolean {
        pushLog("onMyLocationButtonClick", "onMyLocationButtonClick")
        return false
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap!!.setOnMyLocationButtonClickListener(this)
            mMap!!.setOnMyLocationClickListener(this)
            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_ID)
        }


        mMap!!.setOnMapClickListener({ point ->

            mMap!!.clear()
            mMap!!.addMarker(MarkerOptions().position(point))

            locationPoint = point

            setLocationDisplay(point)
            closeFragment(getString(R.string.tag_maps_fragment))

        })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_ID -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mMap!!.setOnMyLocationButtonClickListener(this)
                    mMap!!.setOnMyLocationClickListener(this)
                    mMap!!.isMyLocationEnabled = true
                    mMap!!.uiSettings.isMyLocationButtonEnabled = true
                } else {

                    Toast.makeText(applicationContext, "You have chosen to wander around the map and the settings menu", Toast.LENGTH_SHORT).show()

                }
                return
            }
            else -> {

            }
        }
    }

    private fun setLocationDisplay(point: LatLng?) {
        val locationString = point!!.latitude.toString() + getString(R.string.tag_separate_location) + point.longitude.toString()
        tv_new_event_location.text = locationString
    }

    private fun closeFragment(nameFragment: String) {
        val manager = supportFragmentManager
        manager.popBackStack(nameFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        changedBtn(View.VISIBLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_created_new_event)

        tv_new_event_location.setOnClickListener({ chooseLocation() })
        tv_new_event_date_start.setOnClickListener({ chooseDate(getString(R.string.key_mission_choose_date_start)) })
        tv_new_event_date_expiration.setOnClickListener({ chooseDate(getString(R.string.key_mission_choose_date_expiration)) })
        tv_new_event_time_start.setOnClickListener({ chooseTime(getString(R.string.key_mission_choose_time_start)) })
        tv_new_event_time_expiration.setOnClickListener({ chooseTime(getString(R.string.key_mission_choose_time_expiration)) })

        image_new_event.setOnClickListener(View.OnClickListener {
            chooseImageManager()
        })

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_add_event -> false
                R.id.action_all_event -> {
                    startActivity(Intent(applicationContext, EventListActivity::class.java))
                }
                R.id.action_open_favorites -> {
                    startActivity(Intent(applicationContext
                            , EventListActivity::class.java)
                            .putExtra(getString(R.string.key_mission_open_fragment)
                                    , getString(R.string.key_signed_event_fragment)))
                }
            }
            false
        }
        btn_created_new_event.setOnClickListener({
            if (!checkDataDisplay()) {
                Toast.makeText(applicationContext, "Not all fields are filled", Toast.LENGTH_SHORT).show()
            } else {
                val newEvent: Event = buildDataForWriteInDB()

                if (newEvent.dateStart > newEvent.dateExpiration) {
                    Toast.makeText(applicationContext, "Start date can't be a late end date", Toast.LENGTH_SHORT).show()
                } else {
                    this.myRef = FirebaseDatabase.getInstance().reference.child(getString(R.string.name_table_event_db))
                    val idCreatedEvent = this.myRef.push().key
                    if (eventImageUri.toString() != "") {


                        val photoRef: StorageReference = FirebaseStorage.getInstance().getReference("event_photos").child(idCreatedEvent)

                        photoRef.putFile(eventImageUri)
                                .addOnSuccessListener { taskSnapshot ->
                                    taskSnapshot.downloadUrl.toString()
                                    newEvent.uriImage = taskSnapshot.downloadUrl.toString()
                                    myRef.child(idCreatedEvent).setValue(newEvent)
                                    pushLog("addOnSuccessListener", taskSnapshot.toString())
                                }
                                .addOnFailureListener { exception ->
                                    pushLog("addOnFailureListener", exception.message.toString())
                                    Toast.makeText(applicationContext, "Event can not be created", Toast.LENGTH_SHORT).show()
                                }

                    } else {
                        myRef.child(idCreatedEvent).setValue(newEvent)
                    }

                    val retrofitApiHelper = RetrofitApiHelper()
                    retrofitApiHelper.sendStartNotification(newEvent, idCreatedEvent, Calendar.getInstance())

                    finish()
                }
            }

        })
    }

    private fun chooseImageManager() {
        PickImageDialog.build(PickSetup(), this).setOnPickCancel(object : IPickCancel {
            override fun onCancelClick() {
                pushLog("onPickResult", "click button Cancel")
                //
            }
        }).show(this)
    }

    override fun onPickResult(result: PickResult) {
        pushLog("onPickResult", result)
        if (result.error == null) {
            eventImageUri = result.uri
            displayImage(eventImageUri)
        } else {
            pushLog("onPickResult", result.error)
        }

    }

    private fun displayImage(eventImageUri: Uri?) {
        Picasso.with(this).load(eventImageUri)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .config(Bitmap.Config.RGB_565)
                .fit()
                .centerCrop()
                .into(image_new_event)
    }

    private fun checkDataDisplay(): Boolean = !((edit_text_new_event_title.text.toString().replace(" ", "") == "")
            or (this.locationPoint.latitude == 0.0)
            or (this.locationPoint.longitude == 0.0)
            or (this.timeExpirationMap.isEmpty())
            or (this.timeStartMap.isEmpty())
            or (this.dateExpirationMap.isEmpty())
            or (this.dateStartMap.isEmpty()))


    private fun buildDataForWriteInDB(): Event {
        val titleNewEvent = edit_text_new_event_title.text.toString()
        val descriptionNewEvent = edit_text_new_event_description.text.toString()
        val fullDateStartNewEvent: Calendar = buildDateStart()
        val fullDateExpirationNewEvent: Calendar = buildDateExpiration()
        val locationNewEvent: LatLng = this.locationPoint

        return Event(FirebaseAuth.getInstance().currentUser!!.uid,
                titleNewEvent,
                descriptionNewEvent,
                locationNewEvent.latitude,
                locationNewEvent.longitude,
                fullDateStartNewEvent.timeInMillis,
                fullDateExpirationNewEvent.timeInMillis,
                Calendar.getInstance().timeInMillis)
    }

    private fun buildDateExpiration(): Calendar {
        val fullDateMap: Map<String, String> = this.dateExpirationMap + this.timeExpirationMap
        val fullDateExpirationCalendar = getCalendar(fullDateMap as MutableMap<String, String>)
        return fullDateExpirationCalendar!!
    }

    private fun buildDateStart(): Calendar {
        val fullDateMap: Map<String, String> = this.dateStartMap + this.timeStartMap
        val fullDateStartCalendar = getCalendar(fullDateMap as MutableMap<String, String>)
        return fullDateStartCalendar!!
    }

    private fun getCalendar(dateMap: Map<String, String>): Calendar? =
            Calendar.getInstance().apply {
                set(Calendar.YEAR, dateMap.get(getString(R.string.key_year_map_date))!!.toInt())
                set(Calendar.MONTH, dateMap.get(getString(R.string.key_month_map_date))!!.toInt())
                set(Calendar.DAY_OF_MONTH, dateMap.get(getString(R.string.key_day_map_date))!!.toInt())
                set(Calendar.HOUR_OF_DAY, dateMap.get(getString(R.string.key_hours_map_date))!!.toInt())
                set(Calendar.MINUTE, dateMap.get(getString(R.string.key_minute_map_date))!!.toInt())
                set(Calendar.SECOND, 0)
            }


    private fun displayMap() {

        changedBtn(View.GONE)
        val options = GoogleMapOptions()
        options.compassEnabled(true).zoomControlsEnabled(true)

        val mMapFragment = SupportMapFragment.newInstance(options)
        mMapFragment.getMapAsync(this)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(getString(R.string.tag_maps_fragment))
        fragmentTransaction.replace(R.id.my_container, mMapFragment, getString(R.string.tag_maps_fragment))
        fragmentTransaction.commit()
    }

    private fun changedBtn(int: Int){
        tv_new_event_location.visibility = int
        tv_new_event_date_start.visibility = int
        tv_new_event_date_expiration.visibility =int
        tv_new_event_time_expiration.visibility = int
        tv_new_event_time_start.visibility = int
        btn_created_new_event.visibility = int
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable("dateStartBundle", this.dateStartMap.toProperties())
        outState.putSerializable("dateExpirationMap", this.dateExpirationMap.toProperties())
        outState.putSerializable("timeStartMap", this.timeStartMap.toProperties())
        outState.putSerializable("timeExpirationMap", this.timeExpirationMap.toProperties())
        outState.putDouble("latitude", this.locationPoint.latitude)
        outState.putDouble("longitude", this.locationPoint.longitude)
        outState.putString("urlImage", this.eventImageUri.toString())


        Log.d(LOG_TAG, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        this.dateStartMap = savedInstanceState.getSerializable("dateStartBundle") as MutableMap<String, String>
        this.dateExpirationMap = savedInstanceState.getSerializable("dateExpirationMap") as MutableMap<String, String>
        this.timeStartMap = savedInstanceState.getSerializable("timeStartMap") as MutableMap<String, String>
        this.timeExpirationMap = savedInstanceState.getSerializable("timeExpirationMap") as MutableMap<String, String>
        this.locationPoint = LatLng(savedInstanceState.getDouble("latitude"), savedInstanceState.getDouble("longitude"))
        this.eventImageUri = Uri.parse(savedInstanceState.getString("urlImage"))

        when {
            this.dateStartMap.isNotEmpty() -> {
                setDateDisplay(getString(R.string.key_mission_choose_date_start), buildDateFromDisplay(this.dateStartMap))
            }

        }
        when {
            this.dateExpirationMap.isNotEmpty() -> {
                setDateDisplay(getString(R.string.key_mission_choose_date_expiration), buildDateFromDisplay(this.dateExpirationMap))
            }

        }
        when {
            this.timeStartMap.isNotEmpty() -> {
                setTimeDisplay(getString(R.string.key_mission_choose_time_start), buildTimeFromDisplay(this.timeStartMap))
            }
        }
        when {

            this.timeExpirationMap.isNotEmpty() -> {
                setTimeDisplay(getString(R.string.key_mission_choose_time_expiration), buildTimeFromDisplay(this.timeExpirationMap))
            }
        }

        if (this.locationPoint.latitude != 0.0 && this.locationPoint.longitude != 0.0) {
            setLocationDisplay(this.locationPoint)
        }
        displayImage(this.eventImageUri)
    }

    private fun pushLog(topic: String, message: Any) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}
