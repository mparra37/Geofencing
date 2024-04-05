package parra.mario.geofencing

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Archivo(private val context: Context, private val nombreUsuario: String) {
    private val fileName = "datos_${nombreUsuario}.csv"
    //private val filePath = context.filesDir.path + File.separator + fileName
    private val filePath = context.getExternalFilesDir(null)?.path + File.separator + fileName
    private val storageReference = FirebaseStorage.getInstance().reference

    init {
        // Create an empty CSV file upon instance creation
        createEmptyFileIfNotExist()
    }

    private fun createEmptyFileIfNotExist() {
        val file = File(filePath)
        if (!file.exists()) {
            try {
                FileOutputStream(file, false).use { output ->
                    // Optional: Add header line or initial content here if needed
                    output.write("".toByteArray())
                }
                Log.d("archivo", "CSV file '$fileName' created successfully.")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("archivo", "Error creating CSV file '$fileName'.")
            }
        } else {
            Log.d("archivo", "CSV file '$fileName' already exists.")
        }
    }

    fun agregarLinea(data: String) {
        val timestamp_number = System.currentTimeMillis()
        try {
            // Appends data to the CSV file with a new line
            FileOutputStream(File(filePath), true).use { output ->
                val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                    Date()
                )
                val line = "$timeStamp, $timestamp_number, $data\n"
                output.write(line.toByteArray())
            }
            Log.d("archivo","Data added to CSV file '$fileName'.")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("archivo","Error adding data to CSV file '$fileName'.")
        }
    }

    fun uploadToFirebaseStorage() {
        val file = Uri.fromFile(File(filePath))
        val userStorageRef = storageReference.child("$nombreUsuario/$fileName")

        userStorageRef.putFile(file)
            .addOnSuccessListener {
                // Handle successful upload
                Log.d("archivo", "File uploaded successfully: ${it.metadata?.path}")
                Toast.makeText(context, "se ha enviado el archivo correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle failed upload
                Log.d("archivo", "Error uploading file: ${it.message}")
            }
    }
}