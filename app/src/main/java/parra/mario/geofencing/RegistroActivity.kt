package parra.mario.geofencing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database


class RegistroActivity : AppCompatActivity() {
    lateinit var btnRegistrar: Button
    lateinit var campo_correo: EditText
    lateinit var campo_contra: EditText
    lateinit var campo_contra2: EditText
    private lateinit var auth: FirebaseAuth;
    lateinit var campo_nombre: EditText
    lateinit var campo_edad: EditText
    lateinit var campo_genero: EditText
    lateinit var database: FirebaseDatabase
    lateinit var ref_usuarios: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        btnRegistrar = findViewById(R.id.btn_registrar);
        campo_correo = findViewById(R.id.et_correo_reg);
        campo_contra = findViewById(R.id.et_contra_reg);
        campo_contra2 = findViewById(R.id.et_contra2_reg);
        campo_nombre = findViewById(R.id.et_nombre_reg);
        campo_edad = findViewById(R.id.et_edad_reg)
        campo_genero = findViewById(R.id.et_genero_reg)

        auth = Firebase.auth

        database = com.google.firebase.ktx.Firebase.database

        ref_usuarios = database.getReference("usuarios")

        btnRegistrar.setOnClickListener {
            registar();
        }
    }

    private fun registar() {
        var correo = campo_correo.text.toString();
        var contra1 = campo_contra.text.toString();
        var contra2 = campo_contra2.text.toString();
        var nombre = campo_nombre.text.toString();
        var edad = campo_edad.text.toString()
        var genero = campo_genero.text.toString()

        if (!correo.isNullOrBlank() && !contra1.isNullOrEmpty() && !contra2.isNullOrEmpty()
            && !nombre.isNullOrEmpty() && !edad.isNullOrEmpty() && !genero.isNullOrEmpty()
        ) {

            if (contra1 == contra2) {

                registrarFirebase(correo, contra1, nombre, edad, genero);

            } else {
                Toast.makeText(this, "la contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Ingresar datos", Toast.LENGTH_SHORT).show();
        }
    }

    private fun registrarFirebase(
        correo: String,
        contra: String,
        nombre: String,
        edad: String,
        genero: String
    ) {
        auth.createUserWithEmailAndPassword(correo, contra)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val usuario = auth.currentUser

                    // Prepare user details map
                    val userDetails = hashMapOf(
                        "nombre" to nombre,
                        "edad" to edad,
                        "genero" to genero,
                        "correo" to correo
                    )

                    val timestamp = System.currentTimeMillis()
                    usuario?.let {
                        // Add a new document with the user's UID
                        ref_usuarios.child(timestamp.toString()).setValue(userDetails)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "${usuario?.email} se registró correctamente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "No se pudo ingresar", Toast.LENGTH_LONG)
                                        .show()
                                }

                            }


                    }
                } else {
                    Toast.makeText(this, "No se pudo ingresar", Toast.LENGTH_LONG).show()
                }
            }
    }

}