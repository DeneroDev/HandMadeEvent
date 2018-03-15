package com.example.denero.handmadeevent

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

import kotlinx.android.synthetic.main.activity_created_new_event.*
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import android.support.v4.app.ActivityCompat
import android.view.View
import com.example.denero.handmadeevent.model.Event
import android.graphics.Bitmap
import com.example.denero.handmadeevent.EventList.EventListActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult

//TODO: CreatedNewEventActivity
/*НЕТ СОХРАНЕНИЯ ПРИ ПОВОРОТЕ
 сохранять ли сделанные фото?
*/
class CreatedNewEventActivity : AppCompatActivity(),
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        DatePicker.onDateFragmentListener,
        TimePicker.onTimeFragmentListener,
        IPickResult {
    override fun onPickResult(result: PickResult?) {

        if (result!!.error == null) {
            eventImageUri = result.uri
            displayImage(eventImageUri)
        } else {
            pushLog("onPickResult", result.error)
        }

    }

    private fun displayImage(eventImageUri: Uri?) {
        Picasso.with(this).load(eventImageUri)
                .config(Bitmap.Config.RGB_565)
                .fit()
                .centerCrop()
                .into(image_new_event)
    }

    private lateinit var myRef: DatabaseReference
    private lateinit var eventImageUri: Uri
    private val SELECT_PHOTO: Int = 5
    private val LOCATION_PERMISSION_REQUEST_ID: Int = 3
    private val LOG_TAG: String = "GOT"
    private val LOG_HEAD: String = CreatedNewEventActivity::class.java.simpleName
    private var mMap: GoogleMap? = null
init {
    this.eventImageUri = Uri.EMPTY

}
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
            when(item.itemId){
                R.id.action_add_event -> {false}
                R.id.action_all_event -> {startActivity(Intent(applicationContext, EventListActivity::class.java))}
                R.id.action_open_favorites -> {startActivity(Intent(applicationContext
                        , EventListActivity::class.java)
                        .putExtra(getString(R.string.key_mission_open_fragment)
                                ,getString(R.string.key_signed_event_fragment)))}
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
                    if (eventImageUri.toString() !=""  ){
                        val idCreatedEvent = this.myRef.push().key

                        val photoRef: StorageReference = FirebaseStorage.getInstance().getReference("event_photos").child(idCreatedEvent)

                        photoRef.putFile(eventImageUri)
                                .addOnSuccessListener { taskSnapshot ->
                                    taskSnapshot.downloadUrl.toString()
                                    newEvent.uriImage=taskSnapshot.downloadUrl.toString()
                                    myRef.child(idCreatedEvent).setValue(newEvent)
                                    pushLog("addOnSuccessListener", taskSnapshot.toString())
                                }
                                .addOnFailureListener { exception ->
                                    pushLog("addOnFailureListener", exception.message.toString())
                                    Toast.makeText(applicationContext, "Event can not be created", Toast.LENGTH_SHORT).show()
                                }

                    }else{
                        myRef.push().setValue(newEvent)
                    }

                    // finish()
                }
            }

        })
    }



    private fun chooseImageManager() {

        PickImageDialog.build(PickSetup(), this).show(this)

    }

    private fun checkDataDisplay(): Boolean = !((edit_text_new_event_title.text.toString().replace(" ", "") == "")
            or (tv_new_event_location.text == "")
            or (tv_new_event_date_start.text == "")
            or (tv_new_event_date_expiration.text == "")
            or (tv_new_event_time_start.text == "")
            or (tv_new_event_time_expiration.text == ""))


    private fun buildDataForWriteInDB(): Event {
        val titleNewEvent = edit_text_new_event_title.text.toString()
        val descriptionNewEvent = edit_text_new_event_description.text.toString()
        val fullDateStartNewEvent: Calendar = buildDateStart()
        val fullDateExpirationNewEvent: Calendar = buildDateExpiration()
        val locationNewEvent: LatLng = buildLocation()

        return Event(FirebaseAuth.getInstance().currentUser!!.uid,
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
