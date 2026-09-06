package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.media.util.SingleLiveEvent
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

    private val _error = SingleLiveEvent<Unit>()
    val error: LiveData<Unit>
        get() = _error

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

                // Сохраняем только после успешного ответа сервера
                edited.value = empty
                _postCreated.value = Unit

                loadPosts()
            } catch (e: IOException) {
                // Пост не сохранился — остаёмся на экране
                _error.value = Unit
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun repost(id: Long) {
        viewModelScope.launch {
            try {
                repository.repost(id)
            } catch (e: IOException) {
                _data.value = _data.value?.copy(error = true)
            }
        }
    }

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
            likes = if (post.likedByMe) {
                post.likes - 1
            } else {
                post.likes + 1
            }
        )

        val newPosts = oldPosts.map {
            if (it.id == id) updatedPost else it
        }

        // Оптимистически обновляем UI
        _data.value = _data.value?.copy(
            posts = newPosts,
            error = false
        )

        viewModelScope.launch {
            try {
                // Отправляем изменение на сервер
                val serverPost = repository.likeById(id)

                // Получаем актуальное состояние с сервера
                val postsFromServer = _data.value?.posts.orEmpty().map {
                    if (it.id == id) serverPost else it
                }

                _data.value = _data.value?.copy(
                    posts = postsFromServer
                )
            } catch (e: IOException) {
                // Сервер не принял изменение —
                // возвращаем старое состояние
                _data.value = _data.value?.copy(
                    posts = oldPosts,
                    error = true
                )
            }
        }
    }

    fun removeByID(id: Long) {
        val oldPosts = _data.value?.posts.orEmpty()

        if (oldPosts.none { it.id == id }) {
            return
        }

        val updatedPosts = oldPosts.filter { it.id != id }

        _data.value = _data.value?.copy(
            posts = updatedPosts,
            empty = updatedPosts.isEmpty(),
            error = false
        )

        viewModelScope.launch {
            try {
                repository.removeByID(id)
            } catch (e: IOException) {
                _data.value = _data.value?.copy(
                    posts = oldPosts,
                    empty = oldPosts.isEmpty(),
                    error = true
                )
            }
        }
    }
}