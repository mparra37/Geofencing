package parra.mario.geofencing

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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices


import java.lang.reflect.Type
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
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import parra.mario.geofencing.databinding.ActivityMapsBinding
import parra.mario.geofencing.databinding.FragmentHomeBinding
import kotlin.random.Random


const val LOCATION_REQUEST_CODE = 123
const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 13f
const val GEOFENCE_RADIUS = 100
const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 days
const val GEOFENCE_DWELL_DELAY = 10 * 1000 // 10 secs

private val TAG = MapsActivity::class.java.simpleName

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var jsonMarkers: String
    lateinit var sharedPreferences: SharedPreferences
    lateinit var gson: Gson
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    private val markers = mutableListOf<Marker>()
    private val geofenceList = mutableListOf<Geofence>()


    //private lateinit var mMap: GoogleMap
    //private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //binding = ActivityMapsBinding.inflate(layoutInflater)
        //setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)


        gson = Gson()

        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        jsonMarkers = gson.toJson(markers)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)

        //removeMarkersFromSharedPreferences()

        val botonMensaje: FloatingActionButton = findViewById(R.id.botonMensaje)

        botonMensaje.setOnClickListener{
            val phoneNumber = "526444474618"
            try {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    private fun loadMarkers() {
        markers.clear()

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
                .radius(40.0)
            map.addCircle(circleOptions)

            // Move the camera with animation
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(markerData.latitude, markerData.longitude), CAMERA_ZOOM_LEVEL))

            marker?.let { markers.add(it) }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GEOFENCE_LOCATION_REQUEST_CODE)
        }

        loadMarkers()

        val obson = LatLng(27.48642, -109.94083)
        //map.addMarker(MarkerOptions().position(obson).title("Ciudad Obregón"))
        val zoomLevel = 13f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(obson, zoomLevel))

        if (!isLocationPermissionGranted()) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                LOCATION_REQUEST_CODE
            )
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true

            //Get last known location data
           /* fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    with(map) {
                        val latLng = LatLng(it.latitude, it.longitude)
                        moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL))
                    }
                } else {
                    with(map) {
                        moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(27.48642, -109.94083),
                                CAMERA_ZOOM_LEVEL
                            )
                        )
                    }
                }
            }*/
        }
        setLongClick(map)
    }

     private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val markerCircleMap = mutableMapOf<Marker, Circle>()


    private fun setLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ubicación de riesgo")

            // Set up the input
            val input = EditText(this)
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
                    .radius(40.0) // radius in meters
                val circle = map.addCircle(circleOptions)

                // Move the camera with animation
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                // Store the marker
                marker?.let {
                    markers.add(it)
                    markerCircleMap[it] = circle
                }

                saveMarkersLocally()



            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

            builder.show()


        }


        map.setOnMarkerClickListener { marker ->
            // Create an AlertDialog for confirmation
            AlertDialog.Builder(this)
                .setTitle("Remover Marcador")
                .setMessage("¿Deseas quitar este marcador?")
                .setPositiveButton("Sí") { dialog, which ->
                    // User clicked "Yes", delete the marker
                    markers.remove(marker)
                    markerCircleMap[marker]?.remove()
                    markerCircleMap.remove(marker)
                    marker.remove()
                    saveMarkersLocally()
                }
                .setNegativeButton("No", null)
                .show()

            true // return true to indicate that we have handled the event
        }

    }







    override fun onRestart() {
        super.onRestart()

        //val sharedPreferences = getSharedPreferences("your_pref_name", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("markers", null)
        val type = object : TypeToken<List<MarkerData>>() {}.type
        val savedMarkerDataList = Gson().fromJson<List<MarkerData>>(json, type)



        //val json = sharedPreferences.getString("markers", null)
        json?.let {
            val type = object : TypeToken<List<MarkerData>>() {}.type
            val markerDataList: List<MarkerData> = Gson().fromJson(json, type)

            markerDataList.forEach { markerData ->
                val markerOptions = MarkerOptions()
                    .position(LatLng(markerData.latitude, markerData.longitude))
                    .title(markerData.title)
                val marker = map.addMarker(markerOptions)
                marker?.let { markers.add(it) }

                // If using clustering, you can add markers to the cluster manager instead
                // clusterManager.addItem(markerData)
            }

            // If using clustering, you need to cluster the items after adding all of them
            // clusterManager.cluster()
        }
    }

    private fun clearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun removeMarkersFromSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.remove("markers")
        editor.apply()
    }



    private fun saveMarkersLocally() {
        val markerDataList = convertMarkersToMarkerData(markers) // Assuming 'markers' is your list of Marker objects
        val jsonMarkers = serializeMarkers(markerDataList)

        val editor = sharedPreferences.edit()
        editor.putString("markers", jsonMarkers)
        editor.apply()

        createGeofencens();
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
                title = "Ubicación de Riesgo"
                // Map other properties as needed
            )
        }
    }

    fun createGeofencens(){
        markers.forEach { marker ->
            geofenceList.add(buildGeofence(marker))
        }

        addGeofences()
    }

    private fun buildGeofence(marker: Marker): Geofence {
        return Geofence.Builder()
            .setRequestId("REMINDER_GEOFENCE_ID") // Set the request ID of the geofence
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val geofencingRequest = getGeofencingRequest()
            val pendingIntent = getGeofencePendingIntent()

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)?.run {
                addOnSuccessListener {
                    // Geofences added
                    // Handle success
                }
                addOnFailureListener {
                    // Failed to add geofences
                    // Handle failure
                }
            }
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(this, GeofenceReceiver::class.java)
        // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling addGeofences() and removeGeofences().
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }




    private fun createGeofence(location: LatLng, key: String, geofencingClient: GeofencingClient){
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
            .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                    Geofence.GEOFENCE_TRANSITION_DWELL).setLoiteringDelay(GEOFENCE_DWELL_DELAY)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        //val intent =  Intent(this, GeofenceReceiver::class.java)
            //.putExtra("key", key)
            //.putExtra("message", "Geovalla detectada!")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    GEOFENCE_LOCATION_REQUEST_CODE
                )
            } else {
                geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, pendingIntent)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == GEOFENCE_LOCATION_REQUEST_CODE) {
            if(permissions.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(
                    this,
                    "Esta aplicación necesita permisos de ubicación",
                    Toast.LENGTH_LONG
                ).show()
                // call request permissions again
            }
        }

        if (requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && (
                        grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                                grantResults[1] == PackageManager.PERMISSION_GRANTED
                        )
            ){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                map.isMyLocationEnabled = true
                onMapReady(map)
            } else {
                Toast.makeText(
                    this,
                    "This apps needs location permission to function",
                    Toast.LENGTH_LONG
                ).show()
                // Request permissions again here
            }
        }
    }


    companion object{

        fun showNotification(context: Context, message: String) {
            //val context = applicationContext // Using requireContext() to get the context
            val CHANNEL_ID = "REMINDER_NOTIFICATION_CHANNEL"
            var notificationId = 1554
            notificationId += Random(notificationId).nextInt(1, 30)

            val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://web.telegram.org/k/#@MRSOBRIO_BOT"))
            //val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
              //  PendingIntent.getActivity(context, 0, urlIntent, PendingIntent.FLAG_IMMUTABLE)
            //} else {
               // PendingIntent.getActivity(context, 0, urlIntent, 0)
            //}

            val pendingIntent = PendingIntent.getActivity(context, 0, urlIntent, PendingIntent.FLAG_IMMUTABLE)


            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_access_alarm_24) // Replace with your app's icon
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

}