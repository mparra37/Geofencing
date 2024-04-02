package parra.mario.geofencing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermisosActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    lateinit var boton_permisos: Button
    private val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2

    // Background location permission is requested separately and not demonstrated here.
    // private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisos)

        boton_permisos = findViewById(R.id.button_request_permission)

        boton_permisos.setOnClickListener {
            requestLocationPermissions();
        }

        if(arePermissionsGranted()){
            navigateToInicioActivity();
        }

    }

    private fun requestLocationPermissions() {
        // Here, thisActivity is the current activity
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            navigateToInicioActivity()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check if both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions are granted
                if (arePermissionsGranted()) {
                    // Now, request for the background location permission after explaining the need for it
                    // This is a simplified check. Consider adding more sophisticated logic to handle permission rationale.
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        // For example, you could show a dialog with an explanation here.
                        showBackgroundLocationExplanationDialog()
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(
                            this, arrayOf<String>(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                        )
                    }
                }
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, continue with the app workflow, for example, start your app's main activity
                navigateToInicioActivity()
            }
        }
    }

    // Implement this method based on your app's UI/UX to explain why the background location is needed
    private fun showBackgroundLocationExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de Ubicación en Segundo Plano")
            .setMessage("Nuestra aplicación necesita acceso a la ubicación en segundo plano para ofrecerte servicios basados en tu localización incluso cuando la aplicación no esté en primer plano. Esto nos ayuda a proporcionarte una mejor experiencia de usuario.")
            .setPositiveButton("Aceptar") { dialog, which ->
                // After the user sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE)
            }
            .create()
            .show()
    }

    private fun navigateToInicioActivity() {
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
        finish()
    }
}