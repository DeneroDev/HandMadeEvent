package com.example.denero.handmadeevent.Notification

import android.util.Log
import com.example.denero.handmadeevent.model.Event
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Fedor on 13.03.2018.
 */

class RetrofitApiHelper() {

    val TAG = "RetroApiHelper"
    val START_ACTION = "start"
    val DELETE_NOTIFICATION_FROM_SERVER = "delete_from_server"

    fun sendStartNotification(event: Event, event_id: String, calendar: Calendar) {
        NotificationSubscription().adminSubscribeOn(event)

        var client = server_client()
        var result = createCallResult(client, event, event_id, calendar, START_ACTION)
        retrofitEnqueue(result)
    }
    fun deleteNotificationFromServer(event: Event, event_id: String, calendar: Calendar) {
        var client = server_client()
        var result = createCallResult(client, event, event_id, calendar, DELETE_NOTIFICATION_FROM_SERVER)
        retrofitEnqueue(result)
    }

    private fun server_client(): RetrofitApiService {
        var client = OkHttpClient().newBuilder()
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build()

        var retrofit = Retrofit.Builder()
                .baseUrl("http://35.161.16.255/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build()

        return retrofit.create(RetrofitApiService::class.java)
    }

    private fun createCallResult(
            client : RetrofitApiService,
            event: Event,
            event_id: String,
            calendar: Calendar,
            action : String) : Call<String> {

        return client.startNotificationPostRequest(
                action,
                event_id,
                (event.dateStart / 1000).toString(),
                (event.dateExpiration / 1000).toString(),
                calendar.timeZone.id,
                NotificationSubscription().createTopic(event))
    }

    private fun retrofitEnqueue(result: Call<String>){
        result.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Success!")
                } else Log.i(TAG, "Code: " + response.code().toString())
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.i(TAG, "onFailure")
            }
        })
    }


}