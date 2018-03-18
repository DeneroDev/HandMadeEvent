package com.example.denero.handmadeevent.Notification

import android.util.Log
import com.example.denero.handmadeevent.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Fedor on 13.03.2018.
 */

//Создание(отсылка запроса сервер) уведомления о начале эвента.
//Костыльно добавил отправку информации о запланированном удалении на сервер
class RetrofitApiHelper() {

    val TAG = "RetroApiHelper"
    val START_ACTION = "start"
    val DELETE_ACTION = "delete"

    fun sendStartNotification(event: Event, event_id : String, calendar: Calendar){
        NotificationSubscription().adminSubscribeOn(event)

        resulAndEnqueue(START_ACTION, event, event_id, calendar, 0)
    }

    fun resulAndEnqueue(action : String, event: Event, event_id : String, calendar: Calendar, inc : Int) {
        var client = RetrofitApiService.create()
        Log.i(TAG, action)
        var actionTime : Long = 0
        var topic = ""
        when(action) {
            START_ACTION -> {
                actionTime = event.dateStart
                topic = NotificationSubscription().createTopic(event)
            }
            DELETE_ACTION -> {
                actionTime = event.dateExpiration
                topic = NotificationSubscription().createAdminTopic(event)
            }
        }

        var result = client.startNotificationPostRequest(
                action,
                event_id,
                (actionTime / 1000).toString(),
                calendar.timeZone.id,
                topic)

        result.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful){
                    Log.i(TAG, "Success!")
                    if (inc == 0) resulAndEnqueue(DELETE_ACTION, event, event_id, calendar, inc + 1)
                }
                else Log.i(TAG, "Code: " + response.code().toString())
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.i(TAG, "onFailure")
            }
        })
    }
}