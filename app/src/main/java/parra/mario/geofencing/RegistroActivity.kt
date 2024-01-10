package parra.mario.geofencing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class RegistroActivity : AppCompatActivity() {
    lateinit var btnRegistrar: Button
    lateinit var campo_correo: EditText
    lateinit var campo_contra: EditText
    lateinit var campo_contra2: EditText
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        btnRegistrar = findViewById(R.id.btn_registrar);
        campo_correo = findViewById(R.id.et_correo_reg);
        campo_contra = findViewById(R.id.et_contra_reg);
        campo_contra2 = findViewById(R.id.et_contra2_reg);

        auth = Firebase.auth

        btnRegistrar.setOnClickListener{
            registar();
        }
    }

    private fun registar(){
        var correo = campo_correo.text.toString();
        var contra1 = campo_contra.text.toString();
        var contra2 = campo_contra2.text.toString();

        if(!correo.isNullOrBlank() && !contra1.isNullOrEmpty() && !contra2.isNullOrEmpty()){

            if(contra1 == contra2){

                registrarFirebase(correo, contra1);

            }else{
                Toast.makeText(this, "la contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, "Ingresar datos", Toast.LENGTH_SHORT).show();
        }
    }

    private fun registrarFirebase(correo: String, contra: String){
        auth.createUserWithEmailAndPassword(correo, contra)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    val usuario = auth.currentUser
                    Toast.makeText(this, "${usuario?.email} se registró correctamente", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "No se pudo ingresar", Toast.LENGTH_LONG).show()
                }
            }
    }
}