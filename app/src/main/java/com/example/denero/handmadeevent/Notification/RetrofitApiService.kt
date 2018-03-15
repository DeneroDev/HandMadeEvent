package com.example.denero.handmadeevent.Notification

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * Created by Fedor on 03.03.2018.
 */

interface RetrofitApiService {

    @FormUrlEncoded
    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("handMadeEvent/index.php")
    fun startNotificationPostRequest(
            @Field("event_key") key : String,
            @Field("event_time") time : String,
            @Field("timeZoneId") timeZone : String,
            @Field("notificationTopic") topic : String) : Call<String>


    companion object {
        var client = OkHttpClient().newBuilder()
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build()

        fun create() : RetrofitApiService {
            var retrofit = Retrofit.Builder()
                    .baseUrl("http://35.161.16.255/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build()

            return retrofit.create(RetrofitApiService::class.java)
        }
    }
}