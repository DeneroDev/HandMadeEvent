package com.example.denero.handmadeevent.Notification


import com.example.denero.handmadeevent.model.Event
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Created by Fedor on 15.03.2018.
 */
class NotificationSubscription() {

    fun subscribeOn(event : Event) {
        FirebaseMessaging.getInstance().subscribeToTopic(event.createTopic())
    }

    fun unsubscribeOn(event : Event) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(event.createTopic())
    }

}