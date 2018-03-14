package com.example.denero.handmadeevent.Notification

import android.provider.ContactsContract
import android.util.EventLog
import android.util.Log
import com.example.denero.handmadeevent.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Fedor on 13.03.2018.
 */

//Создание(отсылка запроса сервер) уведомления о начале эвента.
class RetrofitApiHelper(
        private var event: Event,
        private var event_id : String,
        private var calendar: Calendar ) {

    val TAG = "RetroApiHelper"

    fun sendStartNotification(){
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