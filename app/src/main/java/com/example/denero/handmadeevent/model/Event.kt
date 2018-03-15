package com.example.denero.handmadeevent.model

import android.net.Uri

/**
 * Created by Denero on 05.03.2018.
 */
open class Event(){
    lateinit var userCreated: String
    lateinit var titleEvent: String
    lateinit var description: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var dateStart: Long = 0
    var dateExpiration: Long = 0
    var createdTimeInMillis : Long = System.currentTimeMillis()
    var uriImage: String =""

    constructor( userCreated: String,
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

}