package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {

    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        published = "21 мая в 18:36",
        content = "Привет, это новая Нетология!...",
        likes = 100,
        likeByMe = false,
        reposts = 5399
    )

    private val  data = MutableLiveData(post)

    override fun get() = data

    override fun like() {
        post = post.copy(likeByMe = !post.likeByMe, likes = if (post.likeByMe) post.likes - 1 else post.likes + 1 )
        data.value = post
    }

    override fun repost() {
        post = post.copy(reposts = post.reposts + 1)
        data.value = post
    }
}