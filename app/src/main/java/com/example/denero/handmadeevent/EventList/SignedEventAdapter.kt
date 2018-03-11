package com.example.denero.handmadeevent.EventList

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.denero.handmadeevent.R
import com.example.denero.handmadeevent.model.Event
import kotlinx.android.synthetic.main.item_signed_event.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.io.Serializable
/**
 * Created by goga747 on 11.03.2018.
 */


class SignedEventAdapter(var mLister: onSignedEventAdapterListener,
                      var data: MutableList<HashMap<String, Event>>?)
    : RecyclerView.Adapter<SignedEventAdapter.MyViewHolder>() {
    private val LOG_TAG = "GOT"
    private val LOG_HEAD = SignedEventAdapter::class.java.simpleName

    interface onSignedEventAdapterListener {
        fun getIdSelectedEvent(selectEventId: String)
        fun getIdSelectedEventForUnsubscribe(selectEventId: String)
    }

    fun updateDate(data: MutableList<HashMap<String, Event>>?) {
        this.data = data!!

        this.notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {

        var key = ""
        data?.get(position)!!.keys.forEach {
            key = it

        }
        if (key != "") {
            holder!!.id = key

            //TODO: помечать подписанные event????

            holder.title.text = data?.get(position)!![holder.id]?.titleEvent
            holder.timeStart.text = getTimeString(data?.get(position)!![holder.id]!!.dateStart)

            holder.mainLayout.setOnClickListener(View.OnClickListener {

                mLister.getIdSelectedEvent(holder.id)
            })

            holder.btnUnsubscribe.setOnClickListener(View.OnClickListener {
                pushLog("Click ${holder.id}", "subscribe")
                mLister.getIdSelectedEventForUnsubscribe(holder.id)
            })

        }
    }

    private fun getTimeString(timeMillionsSecond: Long): String? {
        //TODO: добавить удобно варимую дату
        val date = Date(timeMillionsSecond)
        val formatter = SimpleDateFormat("M:d:HH:mm")
        return formatter.format(date)
    }

    override fun getItemCount(): Int = data!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return SignedEventAdapter.MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_signed_event, parent, false))
    }


    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.tv_title_event_item_signed_event
        var timeStart: TextView = view.tv_time_start_event_item_signed_event
        var btnUnsubscribe: Button = view.btn_unsubscribe_item_signed_event
        var mainLayout: ConstraintLayout = view.main_layout_item_signed_event

        lateinit var id: String

    }

    private fun pushLog(topic: String, message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}