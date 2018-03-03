package com.example.denero.handmadeevent.MyCreatedEventList

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.denero.handmadeevent.Event
import com.example.denero.handmadeevent.EventList.EventFullInformationActivity
import com.example.denero.handmadeevent.EventList.EventListAdapter
import com.example.denero.handmadeevent.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.event_list_item.view.*

/**
 * Created by Fedor on 03.03.2018.
 */
class MyCreatedEventListAdapter(var eventList : ArrayList<Event>)
    : RecyclerView.Adapter<MyCreatedEventListAdapter.EventListViewHolder>() {

    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        holder.titleTextView.text = eventList[position].titleEvent

        holder.itemView.setOnClickListener({
            var context = holder.itemView.context
            var intent = Intent(context, AdministrationActivity::class.java)

            var json = Gson().toJson(eventList[position])
            intent.putExtra("eventObject", json)

            context.startActivity(intent)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            EventListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.event_list_item,
                    parent, false))

    override fun getItemCount() = eventList.size

    class EventListViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var titleTextView = view.event_list_event_title
    }
}