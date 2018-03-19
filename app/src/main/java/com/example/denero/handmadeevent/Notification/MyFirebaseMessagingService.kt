package com.example.denero.handmadeevent.Notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.denero.handmadeevent.EventList.EventListActivity
import com.example.denero.handmadeevent.FullEventMapActivity
import com.example.denero.handmadeevent.R
import com.example.denero.handmadeevent.model.Event
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Fedor on 14.03.2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "MyFirebaseMessService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.size > 0) {
            var item = remoteMessage.data
            when (item["message"].toString()) {
                "start" -> onRecieveDataGetEvent(item["key"].toString())
                "delete" -> deleteEvent(item["key"].toString())
                "unsub" -> NotificationSubscription().unsubscribeOn(item["topic"].toString())
            }

        }

        if (remoteMessage.notification != null) {
            Log.i(TAG, "Notification body: " + remoteMessage.notification!!.body)
        }
    }

    private fun onRecieveDataGetEvent(event_key: String) {
        val myRef = FirebaseDatabase.getInstance().getReference("Events")
        myRef.child(event_key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var event = snapshot.getValue(Event::class.java)!! as Event
                sendNotification(event, event_key)

            }
        })
    }

    private fun sendNotification(event: Event, event_key: String) {

        var notification_id = (event.createdTimeInMillis % 10000000).toInt()

        var intent = createIntentToFullInfoEvent(event, event_key)
        var contentIntent = PendingIntent.getActivity(this, notification_id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        var intentDone = Intent(this, ActionNotificationBtnReceiver::class.java)
        intentDone.putExtra("DONE", true)
        intentDone.putExtra("event_key", event_key)
        intentDone.putExtra("notification_id", notification_id)
        var pendingIntentDone = PendingIntent.getBroadcast(
                this,
                notification_id + 1, intentDone,
                PendingIntent.FLAG_UPDATE_CURRENT)

        var intentLater = Intent(this, ActionNotificationBtnReceiver::class.java)
        intentLater.putExtra("LATER", true)
        intentLater.putExtra("event_key", event_key)
        intentLater.putExtra("notification_id", notification_id)
        var pendingIntentLater = PendingIntent.getBroadcast(
                this, notification_id + 2,
                intentLater,
                PendingIntent.FLAG_UPDATE_CURRENT)

        var intentCancel = Intent(this, ActionNotificationBtnReceiver::class.java)
        intentCancel.putExtra("CANCEL", true)
        intentCancel.putExtra("event_key", event_key)
        intentCancel.putExtra("notification_id", notification_id)
        var pendingIntentCancel = PendingIntent.getBroadcast(
                this,
                notification_id + 3,
                intentCancel,
                PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon_background)
                .setContentTitle(event.titleEvent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentText("Мероприятие началось!")
                .addAction(0, "На месте", pendingIntentDone)
                .addAction(0, "Опоздаю", pendingIntentLater)
                .addAction(0, "Не приду", pendingIntentCancel)
                .setContentIntent(contentIntent)

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification_id, builder.build())
    }

    //Создание интента для отображения полной информации о эвенте
    //Изменить по необходимости
    private fun createIntentToFullInfoEvent(event: Event, event_key: String): Intent {
        var intent = Intent(this, FullEventMapActivity::class.java)
        intent.putExtra("eventId", event_key)
        return intent
    }

    fun deleteEvent(selectEventId: String) {
        removeEvent(selectEventId)
        removeRecordAttendees(selectEventId)
    }

    private fun removeEvent(selectEventId: String) {
        val myRef = FirebaseDatabase.getInstance().reference
                .child(getString(R.string.name_table_event_db)
                        + getString(R.string.tag_separate_query_db)
                        + selectEventId).removeValue()
    }

    private fun removeRecordAttendees(selectEventId: String) {
        if (selectEventId != "") {
            val refAttendees = FirebaseDatabase.getInstance().reference
            refAttendees.child(getString(R.string.name_table_attendees_event_db))
                    .orderByChild(selectEventId)
                    .equalTo(selectEventId)
                    .addChildEventListener(object : ChildEventListener {
                        override fun onCancelled(snapshot: DatabaseError?) {

                        }

                        override fun onChildMoved(snapshot: DataSnapshot?, p1: String?) {

                        }

                        override fun onChildChanged(snapshot: DataSnapshot?, p1: String?) {

                        }

                        override fun onChildAdded(snapshot: DataSnapshot?, p1: String?) {


                            FirebaseDatabase.getInstance().reference
                                    .child(getString(R.string.name_table_attendees_event_db)
                                            + getString(R.string.tag_separate_query_db)
                                            + snapshot!!.key
                                            + getString(R.string.tag_separate_query_db)
                                            + selectEventId).removeValue()

                        }

                        override fun onChildRemoved(snapshot: DataSnapshot?) {

                        }

                    })
        }
    }
}
