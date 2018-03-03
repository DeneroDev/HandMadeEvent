package com.example.denero.handmadeevent.Notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.denero.handmadeevent.MainWindowActivity
import com.example.denero.handmadeevent.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){

    val TAG = "MyFirebaseMessService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "FROM: " + remoteMessage.from)

        if (remoteMessage.data.size > 0){
            var item = remoteMessage.data
            sendNotification(item["message"], item["eventTitle"])
        }

        if (remoteMessage.notification != null){
            Log.i(TAG, "Notification body: " + remoteMessage.notification!!.body)
        }
    }

    private fun sendNotification(text : String?, title: String?) {
        var intent = Intent(this, MainWindowActivity::class.java)

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var notificationBuider = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuider.build())
    }
}
