package com.example.denero.handmadeevent

import java.util.*

//Добивил поле createdTimeInMillis и функцию createTopic()
class Event(var userCreated: String,
            var titleEvent: String,
            var description: String,
            var latitude: Double,
            var longitude: Double,
            var dateStart: Long,
            var dateExpiration: Long,
            var createdTimeInMillis : Long) {


    constructor() : this("","","",0.0,0.0,0,0, 0)

    fun createTopic() : String{
        return titleEvent + createdTimeInMillis.toString()
    }
}