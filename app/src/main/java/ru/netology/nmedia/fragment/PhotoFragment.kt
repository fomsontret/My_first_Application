package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photo = view.findViewById<ImageView>(R.id.photo)

        val url = requireArguments().getString(ARG_URL)
            ?: return

        Glide.with(this)
            .load(url)
            .into(photo)
    }

    companion object {
        private const val ARG_URL = "url"

        fun newInstance(url: String): PhotoFragment {
            return PhotoFragment().apply {
                arguments = bundleOf(
                    ARG_URL to url
                )
            }
        }
    }
}