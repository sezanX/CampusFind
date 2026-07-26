package com.sezanx.campusfind.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.sezanx.campusfind.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import java.util.UUID

class PostRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // NOTE: This should typically be injected, but keeping it simple for the beginner template
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.imgbb.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imgbbService = retrofit.create(ImgbbService::class.java)

    fun getAllPosts(): Flow<List<Post>> = callbackFlow {
        val subscription = firestore.collection("Posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
                    trySend(posts).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun createPost(context: Context, post: Post, imageUri: Uri?, imgbbApiKey: String): Result<Unit> = suspendCoroutine { continuation ->
        val postId = firestore.collection("Posts").document().id
        val newPost = post.copy(postId = postId)

        if (imageUri != null) {
            // Convert URI to File
            val file = getFileFromUri(context, imageUri)
            if (file != null) {
                // Upload to ImgBB
                GlobalScope.launch {
                    try {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                        
                        val response = imgbbService.uploadImage(imgbbApiKey, body)
                        if (response.isSuccessful && response.body()?.success == true) {
                            val imageUrl = response.body()?.data?.url
                            val postWithImage = newPost.copy(imageUrl = imageUrl ?: "")
                            savePostToFirestore(postWithImage, continuation)
                        } else {
                            continuation.resume(Result.failure(Exception("ImgBB Upload Failed: ${response.message()}")))
                        }
                    } catch (e: Exception) {
                        continuation.resume(Result.failure(e))
                    }
                }
            } else {
                continuation.resume(Result.failure(Exception("Failed to read image file")))
            }
        } else {
            savePostToFirestore(newPost, continuation)
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "upload_${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            Log.e("PostRepository", "Error getting file from URI", e)
            null
        }
    }

    private fun savePostToFirestore(post: Post, continuation: Continuation<Result<Unit>>) {
        firestore.collection("Posts").document(post.postId)
            .set(post)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
    }

    fun deletePost(postId: String, onComplete: (Boolean) -> Unit) {
        firestore.collection("Posts").document(postId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
