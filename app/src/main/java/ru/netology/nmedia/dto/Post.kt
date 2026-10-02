package ru.netology.nmedia.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val published: String = "",
    val content: String = "",
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val reposts: Int = 0,
    val video: String? = null,
    val authorAvatar: String? = null,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val type: AttachmentType
)