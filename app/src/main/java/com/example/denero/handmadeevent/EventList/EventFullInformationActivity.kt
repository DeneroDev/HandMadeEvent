package com.example.denero.handmadeevent.EventList

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.denero.handmadeevent.Event
import com.example.denero.handmadeevent.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_event_full_information.*

class EventFullInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_full_information)

        var jsonEventObject = intent.extras.getString("eventObject")
        var event = Gson().fromJson(jsonEventObject, Event::class.java)

        updateView(event)

        event_full_info_subscribe_btn.setOnClickListener({
            subscribeOnEventNotification(event)
        })

        event_full_info_unsub_btn.setOnClickListener({
            unsubscribeOnEventNotification(event)
        })
    }

    private fun updateView(event: Event){
        event_full_info_title.text = event.titleEvent
    }

    //вызвать функцию при подписке
    private fun subscribeOnEventNotification(event: Event) {
        FirebaseMessaging.getInstance().subscribeToTopic(event.createTopic())
    }
    //вызвать функцию при отписке
    private  fun unsubscribeOnEventNotification(event: Event) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(event.createTopic())
    }
}
