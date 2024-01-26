package parra.mario.geofencing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    var usuario: FirebaseUser? = null
    lateinit var btn_registrarse: Button
    lateinit var btn_ingresar: Button
    lateinit var tv_olvideContra: TextView
    lateinit var campo_correo: EditText
    lateinit var campo_contra: EditText
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_registrarse = findViewById(R.id.btn_registrarse);
        btn_ingresar = findViewById(R.id.btn_ingresar);
        tv_olvideContra = findViewById(R.id.tv_olvidasteContra);
        campo_correo = findViewById(R.id.et_correo);
        campo_contra = findViewById(R.id.et_contra)



        auth = Firebase.auth

        //usuario = auth.currentUser

        btn_registrarse.setOnClickListener{
            var intent: Intent = Intent(this, RegistroActivity::class.java);
            startActivity(intent)
        }

        tv_olvideContra.setOnClickListener {
            var intent: Intent = Intent(this, ContrasenaActivity::class.java)
            startActivity(intent)
        }

        btn_ingresar.setOnClickListener {
            var correo = campo_correo.text.toString();
            var contra = campo_contra.text.toString();

            if(!correo.isNullOrEmpty() && !contra.isNullOrEmpty()){
                auth.signInWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){

                            usuario = auth.currentUser;

                            //var intent: Intent = Intent(this, InicioActivity::class.java); /
                             var intent: Intent = Intent(this, MapsActivity::class.java);
                            startActivity(intent);

                        }else{
                            Toast.makeText(this, "Error al ingresar", Toast.LENGTH_LONG).show();
                        }
                    }
            }else{
                Toast.makeText(this, "Ingresar datos", Toast.LENGTH_LONG).show();
            }



        }

    }



    override fun onRestart() {
        super.onRestart()

        //usuario = auth.currentUser;

        if(usuario!= null){
            var intent: Intent = Intent(this, MapsActivity::class.java);
            startActivity(intent);
        }
    }
}