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

    fun unsubscribeOn(topic : String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }

    fun adminSubscribeOn(event : Event) {
        FirebaseMessaging.getInstance().subscribeToTopic(createAdminTopic(event))
    }

    fun adminUnsubscribeOn(event : Event) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(createAdminTopic(event))
    }

    fun createTopic(event: Event) : String{

        return (event.dateStart + event.createdTimeInMillis).toString()
    }

    fun createAdminTopic(event: Event) : String{
        return "admin" + createTopic(event)
    }

}