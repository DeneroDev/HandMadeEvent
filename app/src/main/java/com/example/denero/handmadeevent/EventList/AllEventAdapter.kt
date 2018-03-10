package com.example.denero.handmadeevent.EventList

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.denero.handmadeevent.R

import com.example.denero.handmadeevent.model.Event
import kotlinx.android.synthetic.main.item_event.view.*
import java.io.Serializable
/**
 * Created by goga747 on 08.03.2018.
 */

class AllEventAdapter(var mLister: onAllEventAdapterListener,
                      var data: MutableList<Event>?)
    : RecyclerView.Adapter<AllEventAdapter.MyViewHolder>() {
    private val LOG_TAG = "GOT"
    private val LOG_HEAD = AllEventAdapter::class.java.simpleName

    interface onAllEventAdapterListener {
        fun getEvent(selectEvent: Event)
    }

    fun updateDate(data: MutableList<Event>?, idInterlocutor: Int) {
        this.data = data!!

        this.notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        holder!!.title.text = data!![position].titleEvent



    }

    override fun getItemCount(): Int = data!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return  AllEventAdapter.MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))
    }


    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.tv_title_event_item_event
        var timeStart: TextView = view.tv_time_start_event_item_event
        var btnSubscribe: Button = view.btn_subscribe_item_event

    }
    private fun pushLog(topic:String,message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}