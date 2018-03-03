package com.example.denero.handmadeevent.EventList

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.denero.handmadeevent.Event
import com.example.denero.handmadeevent.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.event_list_item.view.*

class EventListAdapter(var eventList : ArrayList<Event>)
: RecyclerView.Adapter<EventListAdapter.EventListViewHolder>() {

    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        holder.titleTextView.text = eventList[position].titleEvent

        holder.itemView.setOnClickListener({
            var context = holder.itemView.context
            var intent = Intent(context, EventFullInformationActivity::class.java)

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