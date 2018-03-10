package com.example.denero.handmadeevent.model

/**
 * Created by Denero on 05.03.2018.
 */
open class Event() {
    lateinit var userCreated: String
    lateinit var titleEvent: String
    lateinit var description: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var dateStart: Long = 0
    var dateExpiration: Long = 0


    constructor(userCreated: String,
                titleEvent: String,
                description: String,
                latitude: Double,
                longitude: Double,
                dateStart: Long,
                dateExpiration: Long) : this()

//    constructor(id: String,
//                userCreated: String,
//                titleEvent: String,
//                description: String,
//                latitude: Double,
//                longitude: Double,
//                dateStart: Long,
//                dateExpiration: Long) : this(){
//        this.id = id
//        this.userCreated = userCreated
//        this.titleEvent = titleEvent
//        this.description = description
//        this.latitude = latitude
//        this.longitude = longitude
//        this.dateStart = dateStart
//        this.dateExpiration = dateExpiration
//
//    }

}