package parra.mario.geofencing

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.firebase.database.FirebaseDatabase

class GeofenceEventService : IntentService("GeofenceEventService") {

    override fun onHandleIntent(intent: Intent?) {
        //val action = intent?.action
        val database = FirebaseDatabase.getInstance()
        val refInteracciones = database.getReference("interacciones")

        //when (action) {
            //Geofence.GEOFENCE_TRANSITION_ENTER.toString() -> {
                // Extract data from intent
                val message = intent?.getStringExtra("message")
                val usuario = intent?.getStringExtra("usuario")
                val timestamp = System.currentTimeMillis()

                // Assuming Interaccion is a data class you've defined
                val interaccion = Interaccion(usuario ?: "Unknown", "enter", message ?: "No message")
                refInteracciones.child(timestamp.toString()).setValue(interaccion)
            //}
            // Handle other transitions if needed
        //}
    }
}
