package com.example.denero.handmadeevent.Notification


import com.example.denero.handmadeevent.model.Event
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Created by Fedor on 15.03.2018.
 */
class NotificationSubscription() {

    fun subscribeOn(event : Event) {
        FirebaseMessaging.getInstance().subscribeToTopic(createTopic(event))
    }

    fun unsubscribeOn(event : Event) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(createTopic(event))
    }

    fun createTopic(event: Event) : String{

        return (event.dateStart + event.createdTimeInMillis).toString()
    }

}