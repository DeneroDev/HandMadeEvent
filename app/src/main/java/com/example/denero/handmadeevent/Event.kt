package com.example.denero.handmadeevent

/**
 * Created by goga747 on 02.03.2018.
 */
class Event(var userCreated: String,
            var titleEvent: String,
            var description: String,
            var latitude: Double,
            var longitude: Double,
            var dateStart: Long,
            var dateExpiration: Long) {

    var firebaseKey = ""

    constructor() : this("","","",0.0,0.0,0,0)
}