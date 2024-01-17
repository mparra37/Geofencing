package parra.mario.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.ktx.Firebase

class GeofenceReceiver: BroadcastReceiver() {
    lateinit var key: String
    lateinit var message: String

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent?.hasError() == false) {
                when (geofencingEvent.geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        // Handle enter transition
                        MapsActivity.showNotification(
                            context.applicationContext,
                            "Estas en una zona de riesgo")
                    }
                    Geofence.GEOFENCE_TRANSITION_DWELL -> {
                        // Handle dwell transition
                        MapsActivity.showNotification(
                            context.applicationContext,
                            "Estas en una zona de riesgo")
                    }
                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        MapsActivity.showNotification(
                            context.applicationContext,
                            "Has salido de la zona de riesgo")
                    }
                }
            } else {
                // Handle error scenario
            }
        }
    }
}
