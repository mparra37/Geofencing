package parra.mario.geofencing.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import parra.mario.geofencing.Archivo
import parra.mario.geofencing.CAMERA_ZOOM_LEVEL
import parra.mario.geofencing.GEOFENCE_LOCATION_REQUEST_CODE
import parra.mario.geofencing.GeofenceReceiver
import parra.mario.geofencing.InicioActivity
import parra.mario.geofencing.LOCATION_REQUEST_CODE
import parra.mario.geofencing.LoginActivity
import parra.mario.geofencing.MapsActivity
import parra.mario.geofencing.Marcador
import parra.mario.geofencing.MarkerData
import parra.mario.geofencing.NotificationClickActivity
import parra.mario.geofencing.R
import parra.mario.geofencing.databinding.FragmentHomeBinding
import kotlin.random.Random

class HomeFragment : Fragment(), OnMapReadyCallback {
    private val geofenceList = mutableListOf<Geofence>()
    private var _binding: FragmentHomeBinding? = null
    private var first_time = true
    lateinit var jsonMarkers: String
    lateinit var sharedPreferences: SharedPreferences
    lateinit var gson: Gson
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    private val markers = mutableListOf<Marker>()
    private val marcadores = mutableListOf<Marcador>()
    private val markerCircleMap = mutableMapOf<Marker, Circle>()
    lateinit var archivo: Archivo
    //lateinit var ref_ubicaciones: DatabaseReference
    //lateinit var database: FirebaseDatabase

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        first_time = true

        sharedPreferences = requireContext().getSharedPreferences("preferences_sofia", Context.MODE_PRIVATE)


        gson = Gson()


        //database = Firebase.database

        var usu = "desconocido"
        if(LoginActivity.usuario != null){
            usu = LoginActivity.usuario!!.email!!
            usu = usu.substringBefore('@')
        }

        archivo = Archivo(requireContext(), usu)
        //ref_ubicaciones = database.getReference(usu)
        archivo.agregarLinea("Ver mapa")
        //readMarcadoresFromFirstTime()



        jsonMarkers = gson.toJson(markers)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        val botonMensaje: FloatingActionButton = root.findViewById(R.id.botonMensaje)

        botonMensaje.setOnClickListener{
            archivo.agregarLinea("Clic en icono whatsapp")
            val phoneNumber = "526444474618"
            try {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return root
    }

    private fun saveMarkersLocally(accion: String) {

        val markerDataList = convertMarkersToMarkerData(markers)
        // Assuming 'markers' is your list of Marker objects
        val jsonMarkers = serializeMarkers(markerDataList)

        val editor = sharedPreferences.edit()
        editor.putString("markers", jsonMarkers)
        editor.apply()
        if(accion == "agregar"){
            createGeofencens();
        }

    }

    private fun serializeMarkers(markerDataList: List<MarkerData>): String {
        val gson = Gson()
        return gson.toJson(markerDataList)
    }


    private fun convertMarkersToMarkerData(markers: List<Marker>): List<MarkerData> {
        return markers.map { marker ->
            MarkerData(
                latitude = marker.position.latitude,
                longitude = marker.position.longitude,
                title = marker.title.toString()
                // Map other properties as needed
            )
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true



        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GEOFENCE_LOCATION_REQUEST_CODE)
        }



        val obson = LatLng(27.48642, -109.94083)
        //map.addMarker(MarkerOptions().position(obson).title("Ciudad Obregón"))
        val zoomLevel = 13f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(obson, zoomLevel))

        map.isMyLocationEnabled = true

        //loadMarkers()


        setLongClick(map)

        //readMarcadoresFromFirstTime()
        if(first_time){
            loadMarkers()
            first_time = false
        }
    }

    private fun loadMarkers() {

        val json = sharedPreferences.getString("markers", null)
        val type = object : TypeToken<List<MarkerData>>() {}.type
        val savedMarkerDataList = Gson().fromJson<List<MarkerData>>(json, type)

        savedMarkerDataList?.forEach { markerData ->
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(markerData.latitude, markerData.longitude))
                    .title(markerData.title)
            )

            // Add other properties like circles, info windows, etc.

            // Add a circle around the marker
            val circleOptions = CircleOptions()
                .center(LatLng(markerData.latitude, markerData.longitude))
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(70, 150, 150, 150))
                .radius(100.0)
           val circle =  map.addCircle(circleOptions)

            // Move the camera with animation
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(markerData.latitude, markerData.longitude), CAMERA_ZOOM_LEVEL))

            marker?.let {
                markers.add(it)
                markerCircleMap[it] = circle
            }
        }
        //createGeofencens()
    }


    private fun setLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Ubicación de riesgo")

            // Set up the input
            val input = EditText(context)
            input.hint = "Ingresar nombre"
            builder.setView(input)

            var titulo = "Ubicación de Riesgo"
            builder.setPositiveButton("OK") { dialog, which ->
                titulo = input.text.toString()

                // Add a marker
                val marker = map.addMarker(
                    MarkerOptions().position(latLng).title(titulo)
                )
                // Show info window for the marker
                marker?.showInfoWindow()

                // Add a circle around the marker
                val circleOptions = CircleOptions()
                    .center(latLng)
                    .strokeColor(Color.argb(50, 70, 70, 70)) // semi-transparent gray stroke
                    .fillColor(Color.argb(70, 150, 150, 150)) // lighter gray fill
                    .radius(100.0) // radius in meters
                val circle = map.addCircle(circleOptions)

                // Move the camera with animation
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                // Store the marker
                marker?.let {
                    markers.add(it)
                    markerCircleMap[it] = circle
                }
                var datos: String = "Agregar marcador, ${marker?.title}, ${marker?.position}"
                archivo.agregarLinea(datos)
                val timestamp = System.currentTimeMillis()
                var marcador = Marcador(timestamp,InicioActivity.usuario!!,titulo, latLng.toString())
                //marcadores.add(marcador)
                //ref_ubicaciones.child(timestamp.toString()).setValue(marcador)
                //addMarcadoresToFirebase()
                saveMarkersLocally("agregar")
                Log.d("archivo", "se agregó marcador")
                //createGeofencens()
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

            builder.show()


        }


        map.setOnMarkerClickListener { marker ->
            // Create an AlertDialog for confirmation
            AlertDialog.Builder(context)
                .setTitle("Remover Marcador")
                .setMessage("¿Deseas remover ${marker.title}?")
                .setPositiveButton("Sí") { dialog, which ->
                    // User clicked "Yes", delete the marker

                    //val pos = markers.indexOf(marker)
                    //val marcador = marcadores.get(pos)


                    // Delete from Firebase
                   // if (marcador != null) {
                     //   ref_ubicaciones.child(marcador.timestamp.toString()).removeValue().addOnSuccessListener {
                            // Handle success
                            //Log.d("Firebase", "Marker successfully deleted.")
                    //Toast.makeText(context, "primero", Toast.LENGTH_SHORT).show()
                    var datos: String = "Remover marcador, ${marker?.title}, ${marker?.position}"
                    archivo.agregarLinea(datos)
                    markers.remove(marker)
                    markerCircleMap[marker]?.remove()
                    //Toast.makeText(context, "segundo", Toast.LENGTH_SHORT).show()

                    markerCircleMap.remove(marker)
                    //Toast.makeText(context, "tercero", Toast.LENGTH_SHORT).show()
                    removeGeofence(marker.id.toString())
                    //Toast.makeText(context, "cuarto", Toast.LENGTH_SHORT).show()
                    marker.remove()
                    //marcadores.remove(marcador)
                    Log.d("archivo", "se eliminó marcador")
                    saveMarkersLocally("remover")
                       // }.addOnFailureListener {
                            // Handle failure
                         //   Toast.makeText(context, "No se pudo eliminar el marcador", Toast.LENGTH_SHORT).show()
                        //}
                    //}


                    //saveMarkersLocally()
                }
                .setNegativeButton("No", null)
                .show()

            true // return true to indicate that we have handled the event
        }

    }



    private fun drawMarcadoresOnMap(map: GoogleMap) {
        // Iterate through all marcadores and add them to the map
        marcadores.forEach { marcador ->
            // Parse the position from the string stored in 'posicion'
            // Assuming 'posicion' is stored in the format "lat,lng"
            val parts = marcador.posicion.split(",")
            if (parts.size == 2) {
                val latitude = parts[0].toDoubleOrNull()
                val longitude = parts[1].toDoubleOrNull()

                if (latitude != null && longitude != null) {
                    val position = LatLng(latitude, longitude)

                    // Add a marker for the Marcador position
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(marcador.titulo)
                    )

                    // Optionally, customize your marker (e.g., color, icon)
                    // For example, setting the marker color to red:
                    // marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                    // Add a circle around the marker if needed
                    val circleOptions = CircleOptions()
                        .center(position)
                        .strokeColor(Color.argb(50, 70, 70, 70)) // Semi-transparent gray stroke
                        .fillColor(Color.argb(70, 150, 150, 150)) // Lighter gray fill
                        .radius(40.0) // Radius in meters
                    map.addCircle(circleOptions)

                    // Optionally, keep track of markers and circles if you need to manage them later
                    marker?.let {
                        // Assuming markers is a MutableList<Marker>
                        markers.add(it)
                        // Assuming markerCircleMap is a MutableMap<Marker, Circle>
                        markerCircleMap[it] = map.addCircle(circleOptions)
                    }
                }
            }
        }
        // After adding all markers, you might want to adjust the camera to show them all
        // Or you can skip this step if you prefer manual control over the camera
    }








    fun createGeofencens(){
        //removeAllGeofences()
        geofenceList.clear()
        markers.forEach { marker ->
            geofenceList.add(buildGeofence(marker))
        }

        addGeofences()
    }

    fun removeAllGeofences() {
        if ( ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Assuming getGeofencePendingIntent() returns the PendingIntent used to add geofences
            val pendingIntent = getGeofencePendingIntent()

            geofencingClient.removeGeofences(pendingIntent).run {
                addOnSuccessListener {
                    // Geofences successfully removed
                    // Handle success, e.g., by notifying the user
                    Toast.makeText(context, "All geofences successfully removed", Toast.LENGTH_SHORT).show()
                }
                addOnFailureListener {
                    // Failed to remove geofences
                    // Handle failure, e.g., by notifying the user or logging the error
                    Toast.makeText(context, "Error removing geofences", Toast.LENGTH_SHORT).show()
                    Log.e("GeofenceRemoveError", "Failed to remove all geofences: ${it.message}", it)
                }
            }
        } else {
            // Permission not granted
            Toast.makeText(context, "Location permission is required to remove geofences.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun buildGeofence(marker: Marker): Geofence {
        markerTitles[marker.id] = marker.title ?: "ubicación"
        Log.d("archivo", "id guardado ${marker.id}")
        Log.d("archivo", "title guardado ${marker.title}")
        return Geofence.Builder()
            .setRequestId(marker.id.toString()) // Set the request ID of the geofence
            .setCircularRegion(
                marker.position.latitude,
                marker.position.longitude,
                100f // radius in meters
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private fun addGeofences() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val geofencingRequest = getGeofencingRequest()
            val pendingIntent = getGeofencePendingIntent()

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)?.run {
                addOnSuccessListener {
                    // Geofences added
                    // Handle success
                    //Toast.makeText(context, "te agregó correctamente", Toast.LENGTH_SHORT).show()
                }
                addOnFailureListener {
                    // Failed to add geofences
                    // Handle failure
                    Toast.makeText(context, "Error al agregar marcador", Toast.LENGTH_SHORT).show()
                    Log.d("error_geo", it.stackTrace.toString())
                    Log.d("error_geo", it.cause.toString())
                    Log.d("error_geo", it.toString())
                    Log.d("error_geo", it.message.toString())
                    //Log.d("error_geo", it.printStackTrace())
                    it.printStackTrace()
                }
            }
        }else{
            Toast.makeText(context, "No tiene permisos", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeGeofence(markerId: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.removeGeofences(listOf(markerId)).run {
                addOnSuccessListener {
                    // Geofences successfully removed
                    // Handle success
                    Toast.makeText(context, "Se ha eliminado el marcador correctamente", Toast.LENGTH_SHORT).show()
                }
                addOnFailureListener {
                    // Failed to remove geofences
                    // Handle failure
                    Toast.makeText(context, "Error al eliminar el marcador", Toast.LENGTH_SHORT).show()
                    Log.d("error_geo_remove", it.stackTrace.toString())
                    Log.d("error_geo_remove", it.cause.toString())
                    Log.d("error_geo_remove", it.toString())
                    Log.d("error_geo_remove", it.message.toString())
                    it.printStackTrace()
                }
            }
        } else {
            Toast.makeText(context, "No tiene permisos", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(requireContext(), GeofenceReceiver::class.java)
        // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling addGeofences() and removeGeofences().
        return PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
    }






















    companion object{
        val markerTitles = HashMap<String, String>()
        fun showNotification(context: Context, message: String) {
            //val context = applicationContext // Using requireContext() to get the context
            val CHANNEL_ID = "REMINDER_NOTIFICATION_CHANNEL"
            var notificationId = 1554
            notificationId += Random(notificationId).nextInt(1, 30)

            //https://web.telegram.org/k/#@MRSOBRIO_BOT
            //https://web.telegram.org/k/#@MRsobriobot
            //https://t.me/MRsobriobot
            val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("//https://t.me/MRsobriobot"))
            //intent.setData(Uri.parse("tg://resolve?domain=MRsobriobot"));
            //intent.setPackage("org.telegram.messenger")

            //val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://web.telegram.org/k/#@MRsobriobot"))
            //val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //  PendingIntent.getActivity(context, 0, urlIntent, PendingIntent.FLAG_IMMUTABLE)
            //} else {
            // PendingIntent.getActivity(context, 0, urlIntent, 0)
            //}

            //val pendingIntent = PendingIntent.getActivity(context, 0, urlIntent, PendingIntent.FLAG_IMMUTABLE)

            //val broadcastIntent = Intent(context, NotificationClickedReceiver::class.java)
            //broadcastIntent.setAction("parra.mario.geofencing.NOTIFICATION_CLICKED")
            // Optionally, put extra data
            // Optionally, put extra data
            //broadcastIntent.putExtra("extra_data", "value")

            val interceptIntent = Intent(context, NotificationClickActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, interceptIntent, PendingIntent.FLAG_IMMUTABLE)


            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.play_store_512) // Replace with your app's icon
                .setContentTitle("Ubicación de Riesgo") // Using getString from Fragment
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Ubicación de Riesgo",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Ubicación de Riesgo" }

                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}