package parra.mario.geofencing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class NotificationClickActivity : AppCompatActivity() {
    lateinit var archivo: Archivo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var usu = "desconocido"
        if(LoginActivity.usuario != null){
            usu = LoginActivity.usuario!!.email!!
            usu = usu.substringBefore('@')
        }

        archivo = Archivo(this, usu)
        archivo.agregarLinea("Clic notificacion")
        // Perform any actions here, such as logging the click
        Log.d("archivo", "Notification was clicked")

        // Now redirect to the URL https://web.telegram.org/k/#@MRsobriobot
        val redirectIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://web.telegram.org/k/#@MRSOBRIO_BOT"))
        //val redirectIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://web.telegram.org/k/#@MRsobriobot"))
        startActivity(redirectIntent)

        // Close this activity
        finish()
    }
}
