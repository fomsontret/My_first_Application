package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryImpl()
    val data = repository.getAll()

    fun likeById(id: Long) = repository.likeById(id)
    fun repost(id: Long) = repository.repost(id)
}