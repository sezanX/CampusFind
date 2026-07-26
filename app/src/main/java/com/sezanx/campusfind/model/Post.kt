package com.sezanx.campusfind.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val postId: String = "",
    val type: String = "", // "Lost" or "Found"
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
    val contactEmail: String = "",
    val whatsapp: String = "",
    val createdBy: String = "",
    val createdAt: Timestamp = Timestamp.now()
) : Parcelable
