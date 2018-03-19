package com.example.denero.handmadeevent.Notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.denero.handmadeevent.EventStatistic
import com.example.denero.handmadeevent.model.Event
import com.google.firebase.database.*
import java.util.*


/**
 * Created by Fedor on 14.03.2018.
 */
//Не забыть добавить в манифест

class ActionNotificationBtnReceiver : BroadcastReceiver() {

    val TAG = "ActionBtnReceiver"
    //20 минут отсрочки
    val LATER_TIME = 1200000
    lateinit var contextField : Context
    var notification_id = 0

    override fun onReceive(context: Context, intent: Intent) {

        contextField = context

        var event_key = intent.extras.getString("event_key")
        notification_id =  intent.extras.getInt("notification_id")

        var mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(notification_id)

        val myRef = FirebaseDatabase.getInstance().getReference("Events")

        if (intent.hasExtra("DONE")) {
            Log.i(TAG, "DONE")
            doneAction(myRef, event_key)
        }

        if (intent.hasExtra("LATER")) {
            Log.i(TAG, "LATER")
            laterAction(myRef, event_key)
        }

        if (intent.hasExtra("CANCEL")) {
            Log.i(TAG, "CANCEL")
            cancelAction(myRef, event_key)
        }
    }

    private fun updateCount(database: DatabaseReference, bool: Boolean) {
        database.runTransaction(object : Transaction.Handler {
            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                var mNotificationManager = contextField.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.cancel(notification_id)
            }

            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                if (mutableData.value == null) {
                    mutableData.value =
                            if (bool)
                                EventStatistic(1, 0)
                            else
                                EventStatistic(0, 1)
                } else {
                    var hereCount = Integer.parseInt(mutableData.child("here").value!!.toString())
                    var cancelCount = Integer.parseInt(mutableData.child("cancel").value!!.toString())
                    mutableData.value =
                            if (bool)
                                EventStatistic(hereCount + 1, cancelCount)
                            else
                                EventStatistic(hereCount, cancelCount + 1)
                }
                return Transaction.success(mutableData)
            }


        })
    }

    private fun laterAction(myRef: DatabaseReference, event_key: String) {
        myRef.child(event_key).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var event = snapshot.getValue(Event::class.java) as Event
                var calendar = Calendar.getInstance()
                event.dateStart = calendar.timeInMillis + LATER_TIME

                var retrofitApiHelper = RetrofitApiHelper()
                retrofitApiHelper.sendStartNotification(event, event_key, calendar)
            }
        })
    }

    private fun doneAction(myRef: DatabaseReference, event_key: String) {
        //updateCount(myRef.child(event_key).child("Statistic"), true)
    }

    private fun cancelAction(myRef: DatabaseReference, event_key: String) {
        //updateCount(myRef.child(event_key).child("Statistic"), false)
    }
}