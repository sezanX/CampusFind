package com.sezanx.campusfind.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sezanx.campusfind.databinding.FragmentItemDetailsBinding
import com.sezanx.campusfind.model.Post
import java.text.SimpleDateFormat
import java.util.Locale

class ItemDetailsFragment : Fragment() {

    private var _binding: FragmentItemDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val post = arguments?.getParcelable<Post>("post") ?: return

        binding.tvTitle.text = post.title
        binding.tvDescription.text = post.description
        binding.tvLocation.text = "📍 ${post.location}"
        binding.tvTypeBadge.text = post.type
        
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.tvDate.text = sdf.format(post.date.toDate())

        Glide.with(this)
            .load(post.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade(300))
            .centerCrop()
            .into(binding.ivHero)

        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${post.contactEmail}")
                putExtra(Intent.EXTRA_SUBJECT, "Regarding your CampusFind post: ${post.title}")
            }
            startActivity(intent)
        }

        if (post.whatsapp.isNotBlank()) {
            binding.btnWhatsapp.visibility = View.VISIBLE
            binding.btnWhatsapp.setOnClickListener {
                val cleanNumber = post.whatsapp.replace("+", "").replace(" ", "").replace("-", "")
                val url = "https://wa.me/$cleanNumber"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        } else {
            binding.btnWhatsapp.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
