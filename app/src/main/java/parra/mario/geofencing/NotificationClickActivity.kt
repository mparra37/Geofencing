package parra.mario.geofencing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class NotificationClickActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Perform any actions here, such as logging the click
        Log.d("archivo", "Notification was clicked")

        // Now redirect to the URL
        val redirectIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://web.telegram.org/k/#@MRSOBRIO_BOT"))
        startActivity(redirectIntent)

        // Close this activity
        finish()
    }
}
