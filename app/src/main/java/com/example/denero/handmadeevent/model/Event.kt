package com.example.denero.handmadeevent.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by Denero on 05.03.2018.
 */
open class Event():ClusterItem{
    override fun getPosition(): LatLng {
        return LatLng(latitude,longitude)
    }

    lateinit var userCreated: String
    lateinit var titleEvent: String
    lateinit var description: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var dateStart: Long = 0
    var dateExpiration: Long = 0
    var createdTimeInMillis : Long = 0
    var uriImage: String = ""
    var pathDisk:String =""
    var id:String = ""


    constructor(userCreated: String,
                 titleEvent: String,
                 description: String,
                 latitude: Double,
                 longitude: Double,
                 dateStart: Long,
                 dateExpiration: Long,
                 createdTimeInMillis: Long) : this(){
        this.userCreated=userCreated
        this.titleEvent=titleEvent
        this.description=description
        this.latitude=latitude
        this.longitude=longitude
        this.dateStart=dateStart
        this.dateExpiration=dateExpiration
        this.createdTimeInMillis=createdTimeInMillis
    }

    override fun toString(): String = "\n"+
            this.userCreated + "\n" +
            this.titleEvent + "\n" +
            this.latitude + "\n" +
            this.longitude + "\n" +
            this.dateStart + "\n" +
            this.dateExpiration + "\n" +
            this.createdTimeInMillis + "\n" +
            this.uriImage + "\n"
}