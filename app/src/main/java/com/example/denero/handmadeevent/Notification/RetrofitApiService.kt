package com.example.denero.handmadeevent.Notification

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * Created by Fedor on 03.03.2018.
 */
interface RetrofitApiService {

    @Headers(
            "Content-Type: application/json",
            "Authorization: key=AAAAccsyCAY:APA91bFQXRBV0_-j3-z4x5E4ogqHX6dhGB2QWnjKGKBGMW9TY1_vGeUbip93g64OM0LpLnmx2sHZnMrMSOl5ILDp7h6B0Zl6RSu46p75OmNIN2DTX1mC4K-4MH1XgmLu0MBpHZ4iC17y")
    @POST("fcm/send")
    fun uploadMessageToTopic(@Body body : RequestBody) : Call<String>




    companion object {
        var client = OkHttpClient().newBuilder()
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build()

        fun create() : RetrofitApiService {
            var retrofit = Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            return retrofit.create(RetrofitApiService::class.java)
        }
    }
}