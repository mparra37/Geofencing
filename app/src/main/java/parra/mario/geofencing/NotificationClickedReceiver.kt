package parra.mario.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class NotificationClickedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle the click event here
        Log.d("archivo", "Notification was clicked")
        // Optionally, start an activity or service, update UI, etc.
    }
}