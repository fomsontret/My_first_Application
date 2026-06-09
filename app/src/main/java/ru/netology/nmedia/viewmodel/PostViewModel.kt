package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

private val emptyPost = Post ()

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryImpl()
    val data = repository.getAll()

    fun likeById(id: Long) = repository.likeById(id)
    fun repost(id: Long) = repository.repost(id)

    fun removeById(id: Long) = repository.removeByID(id)

    val edited = MutableLiveData (emptyPost)

    fun save (content: String) {
        edited.value?. let { post ->
            val trimmed: String = content.trim()
            if (trimmed != post.content) {
                repository.save(post.copy(content = content))
            }
            edited.value = emptyPost
        }

    }

    fun edit(post: Post) {
        edited.value = post

    }

    fun cancelEdit() {
        edited.value = emptyPost
    }
}