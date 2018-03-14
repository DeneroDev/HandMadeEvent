package com.example.denero.handmadeevent

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.Toast
import com.example.denero.handmadeevent.Notification.RetrofitApiHelper
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

import kotlinx.android.synthetic.main.activity_created_new_event.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class CreatedNewEventActivity : AppCompatActivity(),
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        DatePicker.onDateFragmentListener,
        TimePicker.onTimeFragmentListener {

    private val LOG_TAG: String = "GOT"
    private val LOG_HEAD: String = CreatedNewEventActivity::class.java.simpleName
    private var mMap: GoogleMap? = null

    override fun onMyLocationClick(p0: Location) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun chooseTime(mission: String) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.key_bundle_missions_pickers), mission)
        val timeDialog = TimePicker()
        timeDialog.arguments = bundle
        timeDialog.show(supportFragmentManager, getString(R.string.tag_time_picker))
    }

    override fun getTime(mission: String, time: String) {
        setTimeDisplay(mission, time)
    }

    private fun setTimeDisplay(mission: String, time: String) {
        when (mission) {
            getString(R.string.key_mission_choose_time_start) -> tv_new_event_time_start.text = time
            getString(R.string.key_mission_choose_time_expiration) -> tv_new_event_time_expiration.text = time
        }
    }

    override fun getDate(mission: String, date: String) {
        setDateDisplay(mission, date)
    }

    private fun setDateDisplay(mission: String, date: String) {
        when (mission) {
            getString(R.string.key_mission_choose_date_start) -> tv_new_event_date_start.text = date
            getString(R.string.key_mission_choose_date_expiration) -> tv_new_event_date_expiration.text = date
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

        mMap!!.setOnMyLocationButtonClickListener(this)
        mMap!!.setOnMyLocationClickListener(this)
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true

        mMap!!.setOnMapClickListener({ point ->
            mMap!!.clear()

            mMap!!.addMarker(MarkerOptions().position(point))

            setLocationDisplay(point)
            closeFragment(getString(R.string.tag_maps_fragment))

        })
    }

    private fun setLocationDisplay(point: LatLng?) {
        val locationString = point!!.latitude.toString() + getString(R.string.tag_separate_location) + point.longitude.toString()
        tv_new_event_location.text = locationString
    }

    private fun closeFragment(nameFragment: String) {
        val manager = supportFragmentManager
        manager.popBackStack(nameFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_created_new_event)

        tv_new_event_location.setOnClickListener({ chooseLocation() })
        tv_new_event_date_start.setOnClickListener({ chooseDate(getString(R.string.key_mission_choose_date_start)) })
        tv_new_event_date_expiration.setOnClickListener({ chooseDate(getString(R.string.key_mission_choose_date_expiration)) })
        tv_new_event_time_start.setOnClickListener({ chooseTime(getString(R.string.key_mission_choose_time_start)) })
        tv_new_event_time_expiration.setOnClickListener({ chooseTime(getString(R.string.key_mission_choose_time_expiration)) })

        btn_created_new_event.setOnClickListener({
            if (!checkDataDisplay()) {
                Toast.makeText(applicationContext, "Not all fields are filled", Toast.LENGTH_SHORT).show()
            } else {
                val newEvent: Event = buildDataForWriteInDB()

                if (newEvent.dateStart > newEvent.dateExpiration) {
                    Toast.makeText(applicationContext, "Start date can't be a late end date", Toast.LENGTH_SHORT).show()
                } else {
                    val myRef = FirebaseDatabase.getInstance().reference.child("Events")
                    var pushRef = myRef.push()
                    pushRef.setValue(newEvent)
                    // finish() запись созданна

                    val retrofitApiHelper = RetrofitApiHelper(newEvent, pushRef.key, Calendar.getInstance())
                    retrofitApiHelper.sendStartNotification()

                }
            }

        })
    }

    private fun checkDataDisplay(): Boolean = !((edit_text_new_event_title.text.toString().replace(" ", "") == "")
            or (tv_new_event_location.text == "")
            or (tv_new_event_date_start.text == "")
            or (tv_new_event_date_expiration.text == "")
            or (tv_new_event_time_start.text == "")
            or (tv_new_event_time_expiration.text == ""))

    //Добавил Calendar.getInstance().timeInMillis в конструктор, т.к. изменился класс Event
    private fun buildDataForWriteInDB(): Event {
        val titleNewEvent = edit_text_new_event_title.text.toString()
        val descriptionNewEvent = edit_text_new_event_description.text.toString()
        val fullDateStartNewEvent: Calendar = buildDateStart()
        val fullDateExpirationNewEvent: Calendar = buildDateExpiration()
        val locationNewEvent: LatLng = buildLocation()
        return Event(FirebaseAuth.getInstance().currentUser!!.email!!,
                titleNewEvent,
                descriptionNewEvent,
                locationNewEvent.latitude,
                locationNewEvent.longitude,
                fullDateStartNewEvent.timeInMillis,
                fullDateExpirationNewEvent.timeInMillis,
                Calendar.getInstance().timeInMillis)
    }

    private fun buildLocation(): LatLng {
        val locationString: String = tv_new_event_location.text.toString()

        val separateLocationList = locationString.split(getString(R.string.tag_separate_location).toRegex())

        return LatLng(separateLocationList[0].toDoubleOrNull()!!, separateLocationList[1].toDoubleOrNull()!!)
    }

    private fun buildDateExpiration(): Calendar {
        val fullDateExpirationString: String = tv_new_event_date_expiration.text.toString() + getString(R.string.tag_separate_date) + tv_new_event_time_expiration.text.toString()

        val separateFullDateExpirationList = fullDateExpirationString.split(getString(R.string.tag_separate_date).toRegex())

        val fullDateExpirationCalendar = getCalendar(separateFullDateExpirationList)
        return fullDateExpirationCalendar!!
    }

    private fun buildDateStart(): Calendar {
        val fullDateStartString: String = tv_new_event_date_start.text.toString() + getString(R.string.tag_separate_date) + tv_new_event_time_start.text.toString()

        val separateFullDateStartList = fullDateStartString.split(getString(R.string.tag_separate_date).toRegex())

        val fullDateStartCalendar = getCalendar(separateFullDateStartList)
        return fullDateStartCalendar!!
    }

    private fun getCalendar(separateDateList: List<String>): Calendar? {
        val fullDateStartCalendar = Calendar.getInstance()
        fullDateStartCalendar.set(Calendar.YEAR, separateDateList[0].toInt())
        fullDateStartCalendar.set(Calendar.MONTH, separateDateList[1].toInt())
        fullDateStartCalendar.set(Calendar.DAY_OF_MONTH, separateDateList[2].toInt())
        fullDateStartCalendar.set(Calendar.HOUR_OF_DAY, separateDateList[3].toInt())
        fullDateStartCalendar.set(Calendar.MINUTE, separateDateList[4].toInt())
        fullDateStartCalendar.set(Calendar.SECOND, 0)
        return fullDateStartCalendar
    }


    private fun displayMap() {
        val options = GoogleMapOptions()
        options.compassEnabled(true).zoomControlsEnabled(true)

        val mMapFragment = SupportMapFragment.newInstance(options)
        mMapFragment.getMapAsync(this)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(getString(R.string.tag_maps_fragment))
        fragmentTransaction.replace(R.id.my_container, mMapFragment, getString(R.string.tag_maps_fragment))
        fragmentTransaction.commit()
    }

    private fun pushLog(topic: String, message: Any) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}
