package parra.mario.geofencing

data class Reminder(
    var key: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var note: String = ""
)
