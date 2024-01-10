package parra.mario.geofencing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    lateinit var btn_registrarse: Button
    lateinit var btn_ingresar: Button
    lateinit var tv_olvideContra: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_registrarse = findViewById(R.id.btn_registrarse);
        btn_ingresar = findViewById(R.id.btn_ingresar);
        tv_olvideContra = findViewById(R.id.tv_olvidasteContra);

        btn_registrarse.setOnClickListener{
            var intent: Intent = Intent(this, RegistroActivity::class.java);
            startActivity(intent)
        }

        tv_olvideContra.setOnClickListener {
            var intent: Intent = Intent(this, ContrasenaActivity::class.java)
            startActivity(intent)
        }

    }
}