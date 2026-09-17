package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.media.util.SingleLiveEvent
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.io.IOException

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    video = "",
    reposts = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(
            AppDb.getInstance(application)
        )

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _error = SingleLiveEvent<Unit>()
    val error: LiveData<Unit>
        get() = _error

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _data.value = _data.value?.copy(
                loading = true,
                error = false
            )

            try {
                val posts = repository.getAll()

                _data.value = FeedModel(
                    posts = posts,
                    loading = false,
                    error = false,
                    empty = posts.isEmpty()
                )
            } catch (e: IOException) {
                _data.value = _data.value?.copy(
                    loading = false,
                    error = true
                )
            }
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)

                loadPosts()
            } catch (e: IOException) {
                _data.value = _data.value?.copy(
                    error = true
                )
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)

                loadPosts()
            } catch (e: IOException) {
                _data.value = _data.value?.copy(
                    error = true
                )
            }
        }
    }

    fun repost(id: Long) {
        viewModelScope.launch {
            try {
                repository.repost(id)
            } catch (e: IOException) {
                _data.value = _data.value?.copy(
                    error = true
                )
            }
        }
    }

    fun save(content: String) {
        val trimmed = content.trim()

        if (trimmed.isEmpty()) {
            return
        }

        val currentPost = edited.value ?: return
        val updatedPost = currentPost.copy(content = trimmed)

        viewModelScope.launch {
            try {
                repository.save(updatedPost)

                edited.value = empty
                _postCreated.value = Unit

                loadPosts()
            } catch (e: IOException) {
                _error.value = Unit
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()

        if (edited.value?.content == text) {
            return
        }

        edited.value = edited.value?.copy(
            content = text
        )
    }
}
