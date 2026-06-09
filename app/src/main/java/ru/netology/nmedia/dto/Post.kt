package ru.netology.nmedia.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val published: String = "",
    val content: String = "",
    val likes: Int = 0,
    val likeByMe: Boolean = false,
    val reposts: Int = 0
)