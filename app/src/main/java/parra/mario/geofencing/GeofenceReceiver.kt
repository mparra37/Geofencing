package parra.mario.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import parra.mario.geofencing.ui.home.HomeFragment
import kotlin.random.Random

class GeofenceReceiver: BroadcastReceiver() {
    lateinit var key: String
    lateinit var message: String
    lateinit var archivo: Archivo
    //lateinit var database: FirebaseDatabase
    //lateinit var ref_interacciones: DatabaseReference




    override fun onReceive(context: Context?, intent: Intent?) {
        var usu = "desconocido"
        if(LoginActivity.usuario != null){
            usu = LoginActivity.usuario!!.email!!
            usu = usu.substringBefore('@')
        }

        archivo = Archivo(context!!, usu)
        if (context != null && intent != null) {


            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent?.hasError() == false) {
                //database = Firebase.database
                //ref_interacciones = database.getReference("interacciones")
                var markerTitle ="titulo"
                geofencingEvent.triggeringGeofences?.forEach { geofence ->
                    Log.d("archivo", "request id ${geofence.requestId}")
                    markerTitle = HomeFragment.markerTitles[geofence.requestId] ?: "titulo"
                    //if (geofence.transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                        // Use the markerTitle for notification or other purposes
                    //}
                }

                when (geofencingEvent?.geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        // Handle enter transition
                        val randomMessage = messages[Random.nextInt(messages.size)]
                        //val timestamp = System.currentTimeMillis()
                        //var interaccion = Interaccion(MapsActivity.usuario!!,"enter", message)
                        //ref_interacciones.child(timestamp.toString()).setValue(interaccion)

                        HomeFragment.showNotification(
                            context.applicationContext,
                            randomMessage)
                        archivo.agregarLinea("Entra a ubicacion, ${markerTitle}, $randomMessage")
                        Log.d("archivo", "entro a ubicación ${markerTitle}, $randomMessage")
                        //val transitionType = geofencingEvent.geofenceTransition
                        //val serviceIntent = Intent(context, GeofenceEventService::class.java)
                        //serviceIntent.action = transitionType.toString()
                        //serviceIntent.putExtra("accion", "enter")

                        // Add additional data to the intent as needed
                        //val randomMessage = "Your message here" // Adjust based on your logic
                        //val usuario = "User identifier" // Adjust based on your logic

                        //serviceIntent.putExtra("message", randomMessage)
                        //serviceIntent.putExtra("usuario", MapsActivity.usuario)

                        // Starting the service
                        //context.startService(serviceIntent)

                    }
                    Geofence.GEOFENCE_TRANSITION_DWELL -> {
                        Log.d("archivo", "se quedó en ubicación")
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("archivo", "salió de ubicación, ${markerTitle}")
                        archivo.agregarLinea("Sale de ubicacion ${markerTitle}")
                    }

                }
            } else {
                // Handle error scenario
            }
        }
    }

    /*val messages = arrayOf(
        "Organiza reuniones y fiestas sin alcohol para demostrar que se puede divertir sin necesidad de bebidas.",
        "Proponte un reto personal de no beber durante un mes y recompénsate con algo que realmente te guste al completarlo.",
        "Sustituye las bebidas alcohólicas por combinados de frutas y agua para mantenerte hidratado y disfrutar de sabores nuevos.",
        "Aprende a reconocer cuándo es suficiente. Conoce tus límites y respétalos.",
        "Experimenta con cócteles sin alcohol. ¡Hay miles de recetas deliciosas y sin resaca!",
        "Rodéate de amigos que también quieran reducir su consumo. El apoyo mutuo es clave.",
        "Crea una lista de reproducción con canciones que te motiven a mantenerte sobrio y feliz.",
        "Convierte la actividad física en tu nueva 'bebida'. El ejercicio libera endorfinas y mejora tu estado de ánimo.",
        "Cuéntales a tus amigos y familiares sobre tu decisión para que te ayuden a mantenerte firme.",
        "Graba un video o escribe tus metas relacionadas con la reducción del alcohol y revísalo cuando necesites recordar tu propósito.",
        "Encuentra actividades que te apasionen y que no incluyan el alcohol. Puedes sorprenderte de cuánto disfrutas sin él.",
        "Toma fotos de tus momentos divertidos sin alcohol y compáralas con las de las noches de exceso. Ver la diferencia puede ser motivador.",
        "Cada vez que evitas beber, date una pequeña recompensa, como un postre especial o un día de relajación.",
        "Comparte tus metas con un amigo cercano para que te recuerde por qué decidiste reducir tu consumo de alcohol.",
        "Imagina los éxitos y logros que obtendrás al reducir el alcohol. ¡Visualízalos como si ya fueran realidad!",
        "Dedica un tiempo diario para desconectar del estrés sin recurrir al alcohol. Puede ser meditación, lectura o simplemente descansar.",
        "Recuerda esos días después de noches sin tomar, cuando te despertaste sintiéndote fresco y lleno de energía.",
        "Habla de lo bien que te sientes cuando tienes la mente despejada y lista para enfrentar cualquier desafío.",
        "Organiza noches divertidas en casa con juegos, películas y bocadillos, pero sin alcohol.",
        "Establece metas alcanzables a corto plazo para mantenerte enfocado y celebrar tus éxitos más a menudo.",
        "Lleva un registro de cada día que no bebes y mira cómo se acumulan los logros.",
        "Identifica las situaciones que te llevan a beber y busca alternativas para afrontarlas.",
        "Descarga aplicaciones que te ayuden a mantener la sobriedad y te ofrezcan apoyo instantáneo.",
        "Enfócate en las ventajas de reducir el alcohol, como una mente más clara, mejor salud y relaciones más fuertes.",
        "Lleva un diario donde escribas tres cosas por las que estás agradecido cada día, para enfocarte en lo positivo.",
        "Encuentra actividades sociales que no giren en torno al alcohol, como clases de baile, deportes o clubes.",
        "Organiza noches de juegos con amigos sin la necesidad de bebidas alcohólicas.",
        "Busca historias inspiradoras de personas que han reducido su consumo y cómo les ha beneficiado.",
        "Encuentra un compañero para tu viaje de reducción de alcohol. Pueden apoyarse mutuamente en momentos difíciles.",
        "Siéntete orgulloso de cada paso que das para reducir el alcohol. ¡Tú eres el dueño de tus decisiones!"
    )*/

    val messages = arrayOf(
        "Organiza reuniones y fiestas sin alcohol para demostrarte que puedes divertirte de manera auténtica, creando momentos que realmente recordarás.",
        "Proponte un reto personal de no beber durante un mes. Imagina lo orgulloso que te sentirás al completarlo, y recompénsate con algo que realmente disfrutes.",
        "Sustituye las bebidas alcohólicas por combinados de frutas y agua. Descubre lo bien que se siente cuidarte mientras disfrutas nuevos sabores.",
        "Reconocer tus límites es clave para mantener el control. ¿Qué cambios positivos has notado cuando respetas tus propios límites?",
        "Explora el mundo de los cócteles sin alcohol y sorpréndete con lo delicioso que pueden ser. ¡Sin resacas, solo buenos momentos!",
        "Rodéate de amigos que también estén comprometidos en reducir su consumo. Juntos, pueden apoyarse para alcanzar sus metas.",
        "Crea una lista de reproducción con canciones que te inspiren a mantenerte sobrio y sentirte bien. Usa la música como una fuente de motivación.",
        "Convierte la actividad física en tu nueva ‘bebida’. Al hacer ejercicio, liberas endorfinas que te harán sentir más feliz y fuerte.",
        "Compartir tu decisión de reducir el alcohol con amigos y familiares puede ayudarte a mantenerte firme. Recuerda, no estás solo en esto.",
        "Graba un video o escribe tus metas para reducir el alcohol y vuelve a verlo cuando necesites recordarte por qué elegiste este camino.",
        "Explora actividades que te apasionen y no incluyan alcohol. ¿Qué has descubierto que disfrutas aún más sin beber?",
        "Toma fotos de tus momentos divertidos sin alcohol y compáralas con las de noches de exceso. Ver cómo ha mejorado tu bienestar puede ser muy motivador.",
        "Cada vez que elijas no beber, date una pequeña recompensa que te haga sentir bien. Es un recordatorio de que cada paso cuenta.",
        "Comparte tus metas con un amigo cercano para que te recuerde lo lejos que has llegado y lo mucho que te importa este cambio.",
        "Imagina los éxitos y logros que obtendrás al reducir el alcohol. ¿Cómo se verá tu vida una vez que alcances esas metas?",
        "Tómate un momento cada día para relajarte sin recurrir al alcohol. ¿Qué actividades te ayudan a desconectar de manera positiva?",
        "Recuerda cómo te sientes después de una noche sin beber. Despiertas fresco, con energía, y listo para aprovechar el día.",
        "Habla de lo bien que te sientes cuando tu mente está despejada y puedes enfrentar cualquier desafío con claridad.",
        "Organiza noches divertidas en casa con amigos, juegos y bocadillos. Al eliminar el alcohol, puedes concentrarte en disfrutar el momento al máximo.",
        "Establece metas pequeñas y alcanzables para mantenerte motivado. Cada éxito te llevará más cerca de tu objetivo final.",
        "Lleva un registro de cada día que no bebes. Ver cómo crecen tus logros te recordará el progreso que has hecho.",
        "Identifica las situaciones que te llevan a beber y piensa en alternativas que te ayuden a afrontarlas sin alcohol. ¿Qué soluciones has encontrado?",
        "Descarga aplicaciones que te ofrezcan apoyo para mantenerte sobrio. No tienes que hacerlo solo, siempre hay ayuda disponible.",
        "Enfócate en los beneficios de reducir el alcohol: una mente más clara, mejor salud y relaciones más fuertes. ¿Qué cambios positivos has notado?",
        "Lleva un diario de gratitud donde escribas tres cosas positivas cada día. Esto te ayudará a centrarte en lo bueno y mantenerte motivado.",
        "Encuentra actividades sociales que no giren en torno al alcohol. ¿Qué nuevas experiencias has descubierto que disfrutas más?",
        "Organiza noches de juegos con amigos donde la diversión sea lo principal, sin necesidad de recurrir al alcohol para pasarla bien.",
        "Busca historias de personas que han reducido su consumo de alcohol y cómo ha mejorado su vida. Inspírate en su camino.",
        "Encuentra un compañero en tu viaje para reducir el alcohol. Pueden apoyarse mutuamente en los momentos difíciles y celebrar juntos sus logros.",
        "Siéntete orgulloso de cada paso que das para reducir el alcohol. Recuerda que cada decisión es tuya y cada logro refleja tu esfuerzo."
    )







}
