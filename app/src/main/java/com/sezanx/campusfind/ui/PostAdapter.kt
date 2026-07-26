package com.sezanx.campusfind.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sezanx.campusfind.databinding.ItemPostBinding
import com.sezanx.campusfind.model.Post
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private var posts: List<Post>,
    private val onItemClick: (Post) -> Unit,
    private val onOptionsClick: ((Post) -> Unit)? = null
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    fun updateData(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvTitle.text = post.title
            binding.tvTypeBadge.text = post.type
            binding.tvLocation.text = "📍 ${post.location}"
            
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvDate.text = sdf.format(post.date.toDate())

            if (post.type.equals("Lost", ignoreCase = true)) {
                binding.tvTypeBadge.setBackgroundResource(com.sezanx.campusfind.R.drawable.bg_badge_lost)
            } else {
                binding.tvTypeBadge.setBackgroundResource(com.sezanx.campusfind.R.drawable.bg_badge_found)
            }

            Glide.with(binding.root.context)
                .load(post.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(binding.ivThumbnail)

            // Random placeholder avatar for demo purposes removed for grid space

            // Show options menu only if the callback is provided (e.g. in ProfileFragment)
            if (onOptionsClick != null) {
                val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                if (post.createdBy == currentUserId) {
                    binding.ivOptions.visibility = android.view.View.VISIBLE
                    binding.ivOptions.setOnClickListener { onOptionsClick.invoke(post) }
                } else {
                    binding.ivOptions.visibility = android.view.View.GONE
                }
            } else {
                binding.ivOptions.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onItemClick(post) }
        }
    }
}
