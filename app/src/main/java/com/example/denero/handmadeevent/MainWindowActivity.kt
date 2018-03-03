package com.example.denero.handmadeevent

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.denero.handmadeevent.EventList.EventListActivity
import com.example.denero.handmadeevent.MyCreatedEventList.MyCreatedEventListActivity
import kotlinx.android.synthetic.main.activity_main_window.*

class MainWindowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_window)

        to_create_event_btn.setOnClickListener({
            startActivity(Intent(applicationContext, CreatedNewEventActivity::class.java))
        })

        to_event_list_btn.setOnClickListener({
            startActivity(Intent(applicationContext, EventListActivity::class.java))
        })

        to_my_created_event_list.setOnClickListener({
            startActivity(Intent(applicationContext, MyCreatedEventListActivity::class.java))
        })

    }
}
