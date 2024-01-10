package parra.mario.geofencing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ContrasenaActivity : AppCompatActivity() {
    lateinit var btn_restablecer: Button
    lateinit var et_correo_cont: EditText
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasena)

        btn_restablecer = findViewById(R.id.btn_restablecer);
        et_correo_cont = findViewById(R.id.et_correo_cont);

        auth = Firebase.auth

        btn_restablecer.setOnClickListener{
            var correo = et_correo_cont.text.toString();

            if(!correo.isNullOrEmpty()){

                auth.sendPasswordResetEmail(correo)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            Toast.makeText(this, "se envió un correo a $correo", Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this, "error al enviar correo", Toast.LENGTH_SHORT).show()
                        }
                    }

            }else{
                Toast.makeText(this, "ingresar un correo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}