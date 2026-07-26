package com.sezanx.campusfind.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sezanx.campusfind.R
import com.sezanx.campusfind.databinding.FragmentCreatePostBinding
import com.sezanx.campusfind.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels()
    
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivPostImage.setImageURI(uri)
            binding.llImagePlaceholder.visibility = View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardImagePicker.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val desc = binding.etDescription.text.toString()
            val loc = binding.etLocation.text.toString()
            val email = binding.etEmail.text.toString()
            val whatsapp = binding.etWhatsapp.text.toString()
            val type = if (binding.toggleGroupType.checkedButtonId == R.id.btnLost) "Lost" else "Found"

            if (title.isBlank() || desc.isBlank() || loc.isBlank()) {
                Toast.makeText(requireContext(), "Please fill out all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.btnSubmit.isEnabled = false

            val imgbbApiKey = "c92ef83f351213827481c244d5af3bc1"
            viewModel.createPost(requireContext(), imgbbApiKey, title, desc, loc, type, email, whatsapp, selectedImageUri)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createPostStatus.collect { result ->
                binding.progressBar.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
                
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Post Created!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
