package com.sezanx.campusfind.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sezanx.campusfind.R
import com.sezanx.campusfind.databinding.FragmentHomeBinding
import com.sezanx.campusfind.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostAdapter(emptyList(), onItemClick = { post ->
            val bundle = Bundle().apply { putParcelable("post", post) }
            findNavController().navigate(R.id.action_homeFragment_to_itemDetailsFragment, bundle)
        })
        
        binding.recyclerViewPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPosts.adapter = adapter

        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val firstName = user?.displayName?.split(" ")?.firstOrNull() ?: "Student"
        binding.tvUserNameHeader.text = firstName

        val avatarUrl = user?.photoUrl?.toString() ?: "https://ui-avatars.com/api/?name=${user?.displayName ?: "User"}&background=4F46E5&color=fff&rounded=true"
        com.bumptech.glide.Glide.with(this)
            .load(avatarUrl)
            .circleCrop()
            .into(binding.ivProfile)

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            filterPosts(text.toString(), getSelectedType())
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, _ ->
            filterPosts(binding.etSearch.text.toString(), getSelectedType())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collect { posts ->
                filterPosts(binding.etSearch.text.toString(), getSelectedType())
            }
        }
    }

    private fun getSelectedType(): String {
        return when (binding.chipGroupFilter.checkedChipId) {
            R.id.chipLost -> "Lost"
            R.id.chipFound -> "Found"
            else -> "All"
        }
    }

    private fun filterPosts(query: String, type: String) {
        val allPosts = viewModel.posts.value
        val filtered = allPosts.filter { post ->
            val matchesSearch = post.title.contains(query, ignoreCase = true)
            val matchesType = type == "All" || post.type == type
            matchesSearch && matchesType
        }
        adapter.updateData(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
