package parra.mario.geofencing.ui.gallery

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import parra.mario.geofencing.Archivo
import parra.mario.geofencing.LoginActivity
import parra.mario.geofencing.R
import parra.mario.geofencing.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {
    lateinit var archivo: Archivo

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var usu = "desconocido"
        if(LoginActivity.usuario != null){
            usu = LoginActivity.usuario!!.email!!
            usu = usu.substringBefore('@')
        }

        archivo = Archivo(requireContext(), usu)
        archivo.agregarLinea("Ver ayuda")

        val step1TextView: TextView = root.findViewById(R.id.step1)
        step1TextView.text = Html.fromHtml(getString(R.string.step1), Html.FROM_HTML_MODE_LEGACY)

        // Paso 2
        val step2TextView: TextView = root.findViewById(R.id.step2)
        step2TextView.text = Html.fromHtml(getString(R.string.step2), Html.FROM_HTML_MODE_LEGACY)

        // Paso 3
        val step3TextView: TextView = root.findViewById(R.id.step3)
        step3TextView.text = Html.fromHtml(getString(R.string.step3), Html.FROM_HTML_MODE_LEGACY)

        // Paso 4
        val step4TextView: TextView = root.findViewById(R.id.step4)
        step4TextView.text = Html.fromHtml(getString(R.string.step4), Html.FROM_HTML_MODE_LEGACY)

        // Paso 5
        val step5TextView: TextView = root.findViewById(R.id.step5)
        step5TextView.text = Html.fromHtml(getString(R.string.step5), Html.FROM_HTML_MODE_LEGACY)

        // Paso 6
        val step6TextView: TextView = root.findViewById(R.id.step6)
        step6TextView.text = Html.fromHtml(getString(R.string.step6), Html.FROM_HTML_MODE_LEGACY)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}