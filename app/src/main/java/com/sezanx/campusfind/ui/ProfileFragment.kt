package com.sezanx.campusfind.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.sezanx.campusfind.R
import com.sezanx.campusfind.databinding.FragmentProfileBinding
import com.sezanx.campusfind.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        
        binding.tvUserName.text = user?.displayName ?: "Campus Student"
        binding.tvUserEmail.text = user?.email ?: "No email"

        val avatarUrl = user?.photoUrl?.toString() ?: "https://ui-avatars.com/api/?name=${user?.displayName ?: "User"}&background=4F46E5&color=fff&rounded=true"
        com.bumptech.glide.Glide.with(this)
            .load(avatarUrl)
            .circleCrop()
            .into(binding.ivAvatar)

        // Notifications Settings (Default to enabled for simplicity)
        binding.switchNotifications.isChecked = true
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("all_posts")
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("all_posts")
            }
        }

        // Sign Out
        binding.btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_global_to_login) // We will need a global action
        }

        // My Posts Grid
        adapter = PostAdapter(emptyList(), onItemClick = { post ->
            val bundle = Bundle().apply { putParcelable("post", post) }
            findNavController().navigate(R.id.action_profileFragment_to_itemDetailsFragment, bundle)
        }, onOptionsClick = { post ->
            showOptionsDialog(post)
        })

        binding.recyclerViewMyPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewMyPosts.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collect { posts ->
                val myPosts = posts.filter { it.createdBy == user?.uid }
                adapter.updateData(myPosts)
            }
        }
    }

    private fun showOptionsDialog(post: com.sezanx.campusfind.model.Post) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(requireContext())
            .setTitle("Post Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Edit
                        Toast.makeText(requireContext(), "Edit not fully implemented yet", Toast.LENGTH_SHORT).show()
                        // Pass post to CreatePostFragment in edit mode
                    }
                    1 -> { // Delete
                        viewModel.deletePost(post.postId)
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
