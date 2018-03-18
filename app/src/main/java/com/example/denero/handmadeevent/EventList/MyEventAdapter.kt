package com.example.denero.handmadeevent.EventList

import android.content.Context
import android.graphics.Bitmap
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.denero.handmadeevent.R
import com.example.denero.handmadeevent.model.Event
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_event.view.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by goga747 on 16.03.2018.
 */
class MyEventAdapter(var mLister: onMyEventAdapterListener,
                     var data: MutableList<HashMap<String, Event>>?,
                     var context: Context,
                     var month: Array<String>)

    : RecyclerView.Adapter<MyEventAdapter.MyViewHolder>() {
    private val LOG_TAG = "GOT"
    private val LOG_HEAD = MyEventAdapter::class.java.simpleName

    interface onMyEventAdapterListener {
        fun getIdSelectedEvent(selectEventId: String)
        fun getIdSelectedEventForRemove(selectEventId: String)
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



            holder.title.text = data?.get(position)!![holder.id]?.titleEvent
            holder.timeStart.text = buildDisplayTime(getTimeString(data?.get(position)!![holder.id]!!.dateStart))

            holder.mainLayout.setOnClickListener(View.OnClickListener {

                mLister.getIdSelectedEvent(holder.id)
            })

            holder.btnRemove.setOnClickListener(View.OnClickListener {
                pushLog("Click ${holder.id}", "Remove")
                mLister.getIdSelectedEventForRemove(holder.id)
            })

            if (!data?.get(position)!![holder.id]?.uriImage!!.isEmpty()) {
                Picasso.with(context).load(data?.get(position)!![holder.id]?.uriImage)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .error(R.mipmap.ic_launcher)
                        .config(Bitmap.Config.RGB_565)
                        .fit()
                        .centerCrop()
                        .into(holder.image)

            }


        }
    }

    private fun getTimeString(timeMillionsSecond: Long): String? {

        val date = Date(timeMillionsSecond)
        val formatter = SimpleDateFormat("M:d:HH:mm")
        return formatter.format(date)
    }

    private fun buildDisplayTime(timeString: String?): String {
        val fullDateExpirationString: String = timeString!!
        val tagSeparate = context.getString(R.string.tag_separate_date)
        val separateFullDateExpirationList = fullDateExpirationString.split((tagSeparate).toRegex())


        return month[separateFullDateExpirationList[0].toInt()] +
                tagSeparate +
                separateFullDateExpirationList[1] +
                tagSeparate +
                separateFullDateExpirationList[2] +
                tagSeparate +
                separateFullDateExpirationList[3]

    }

    override fun getItemCount(): Int = data!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyEventAdapter.MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_event, parent, false))
    }


    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.tv_title_event_item_my_event
        var timeStart: TextView = view.tv_time_start_event_item_my_event
        var btnRemove: Button = view.btn_remove_item_my_event
        var mainLayout: ConstraintLayout = view.main_layout_item_my_event
        var image: ImageView = view.image_item_my_event

        lateinit var id: String

    }

    private fun pushLog(topic: String, message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}