package com.example.denero.handmadeevent.MyCreatedEventList

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.denero.handmadeevent.Event
import com.example.denero.handmadeevent.R
import com.example.denero.handmadeevent.Notification.RetrofitApiService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_administration.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdministrationActivity : AppCompatActivity() {

    val TAG = "AdministrationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administration)

        var jsonEventObject = intent.extras.getString("eventObject")
        var event = Gson().fromJson(jsonEventObject, Event::class.java)

        updateView(event)

        administration_send_notification_btn.setOnClickListener({
            var client = RetrofitApiService.create()

            var textMessage = "Hello my subscribers!"
            if (!administration_message_edit_text.text.isEmpty()) {
                textMessage = administration_message_edit_text.text.toString()
                administration_message_edit_text.setText("")
            }

            var body = createNotificatioBody(textMessage, event)
            var result = client.uploadMessageToTopic(body)

            result.enqueue(object : Callback<String>{
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<String>?, response: Response<String>) {
                    if (response.isSuccessful) {
                        Log.i(TAG, response.body())
                        Toast.makeText(applicationContext, "Notification sent!", Toast.LENGTH_SHORT).show()
                    }
                    else
                        Log.i(TAG, "Code: " + response.code().toString())
                }
            })
        })

    }

    private fun updateView(event: Event){
        administration_event_title.text = event.titleEvent
    }

    private fun createNotificatioBody(text : String, event: Event) : RequestBody {
        var JSON = MediaType.parse("application/json; charset=utf-8")

        var message = JSONObject()
        message.put("to", "/topics/" + event.firebaseKey)

        var data = JSONObject()
        data.put("message", text)
        data.put("eventTitle", event.titleEvent)

        message.put("data", data)

        return RequestBody.create(JSON, message.toString())
    }
}
