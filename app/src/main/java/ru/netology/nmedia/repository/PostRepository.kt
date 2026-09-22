package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    suspend fun getAll(): List<Post>
    suspend fun getNewer(): Int
    suspend fun getNewPostsCount(): Int
    suspend fun showNewPosts()

    suspend fun likeById(id: Long): Post
    suspend fun repost(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
}