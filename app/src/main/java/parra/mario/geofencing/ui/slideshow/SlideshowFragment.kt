package parra.mario.geofencing.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import parra.mario.geofencing.Archivo
import parra.mario.geofencing.LoginActivity
import parra.mario.geofencing.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {
    lateinit var archivo: Archivo
    private var _binding: FragmentSlideshowBinding? = null
    lateinit var btn_enviar_datos: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var usu = "desconocido"
        if(LoginActivity.usuario != null){
            usu = LoginActivity.usuario!!.email!!
            usu = usu.substringBefore('@')
        }

        archivo = Archivo(requireContext(), usu)
        archivo.agregarLinea("Ver acerca de")


        btn_enviar_datos = binding.btnEnviarDatos

        btn_enviar_datos.setOnClickListener {
            archivo.agregarLinea("Enviar datos")
            Toast.makeText(context, "enviando datos", Toast.LENGTH_SHORT).show()
            archivo.uploadToFirebaseStorage()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}