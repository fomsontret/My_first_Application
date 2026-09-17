package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun repost(id: Long)
    suspend fun removeByID(id: Long)
    suspend fun save(post: Post)
}