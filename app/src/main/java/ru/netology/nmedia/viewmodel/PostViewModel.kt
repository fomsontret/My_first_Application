package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.media.util.SingleLiveEvent
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import java.io.IOException
import kotlin.concurrent.thread

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

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun save(content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {

            return
        }

        val currentPost = edited.value ?: return
        val updatedPost = currentPost.copy(content = trimmed)

        thread {
            try {
                repository.save(updatedPost)
                _postCreated.postValue(Unit)
                loadPosts()

                edited.postValue(empty)
            } catch (e: IOException) {
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun repost(id: Long) = repository.repost(id)

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        val oldPosts = _data.value?.posts.orEmpty()
        val post = oldPosts.firstOrNull { it.id == id } ?: return

        val updatedPost = post.copy(
            likedByMe = !post.likedByMe,
            likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
        )
        val newPosts = oldPosts.map { if (it.id == id) updatedPost else it }
        _data.value = _data.value?.copy(posts = newPosts)

        thread {
            try {
                val serverPost = repository.likeById(id)
                val postsFromServer = _data.value?.posts.orEmpty().map {
                    if (it.id == id) serverPost else it
                }
                _data.postValue(_data.value?.copy(posts = postsFromServer))
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = oldPosts))
            }
        }
    }

    fun removeByID(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeByID(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}