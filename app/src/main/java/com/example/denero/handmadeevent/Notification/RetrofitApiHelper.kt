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
class RetrofitApiHelper() {

    val TAG = "RetroApiHelper"

    fun sendStartNotification(event: Event, event_id : String, calendar: Calendar){
        var client = RetrofitApiService.create()

        var result = client.startNotificationPostRequest(
                event_id,
                event.dateStart.toString(),
                calendar.timeZone.id,
                event.createTopic())

        result.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful)
                    Log.i(TAG, "Success!")
                else Log.i(TAG, "Code: " + response.code().toString())
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.i(TAG, "onFailure")
            }
        })
    }
}