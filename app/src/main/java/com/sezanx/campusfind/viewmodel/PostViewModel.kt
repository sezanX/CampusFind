package com.sezanx.campusfind.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sezanx.campusfind.model.Post
import com.sezanx.campusfind.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val repository = PostRepository()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _createPostStatus = MutableSharedFlow<Result<Unit>>()
    val createPostStatus: SharedFlow<Result<Unit>> = _createPostStatus.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.getAllPosts().collect { postList ->
                _posts.value = postList
            }
        }
    }

    fun createPost(
        context: android.content.Context,
        imgbbApiKey: String,
        title: String,
        description: String,
        location: String,
        type: String,
        email: String,
        whatsapp: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            val post = Post(
                type = type,
                title = title,
                description = description,
                location = location,
                contactEmail = email,
                whatsapp = whatsapp,
                createdBy = user?.uid ?: ""
            )
            val result = repository.createPost(context, post, imageUri, imgbbApiKey)
            _createPostStatus.emit(result)
        }
    }

    fun deletePost(postId: String) {
        repository.deletePost(postId) { success ->
            // Post list automatically updates via SnapshotListener
        }
    }
}
