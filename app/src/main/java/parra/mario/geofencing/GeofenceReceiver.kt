package parra.mario.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class GeofenceReceiver: BroadcastReceiver() {
    lateinit var key: String
    lateinit var message: String

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent?.hasError() == false) {


                when (geofencingEvent.geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        // Handle enter transition
                        val randomMessage = messages[Random.nextInt(messages.size)]
                        MapsActivity.showNotification(
                            context.applicationContext,
                            randomMessage)
                    }
                    /*
                   Geofence.GEOFENCE_TRANSITION_DWELL -> {
                        // Handle dwell transition
                       val randomMessage = messages[Random.nextInt(messages.size)]
                        MapsActivity.showNotification(
                            context.applicationContext,
                            randomMessage + " - https://web.telegram.org/k/#@MRSOBRIO_BOT")
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        MapsActivity.showNotification(
                            context.applicationContext,
                            "Has salido de la zona de riesgo")
                    }*/
                }
            } else {
                // Handle error scenario
            }
        }
    }

    val messages = arrayOf(
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
    )






}
