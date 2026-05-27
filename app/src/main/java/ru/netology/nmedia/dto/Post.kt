package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    var likes: Int = 0,
    var likeByMe: Boolean = false,
    var reposts: Int = 0
)