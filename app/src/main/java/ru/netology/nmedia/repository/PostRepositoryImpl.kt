package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {

    private var posts = listOf(

        Post (
            id = 7,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...",
            likes = 1008,
            likeByMe = false,
            reposts = 5399
        ),

        Post (
            id = 6,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...",
            likes = 170,
            likeByMe = false,
            reposts = 5399
        ),

        Post (
            id = 5,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...",
            likes = 600,
            likeByMe = false,
            reposts = 5399
        ),

        Post (
            id = 4,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...Привет, это новая Нетология!...",
            likes = 500,
            likeByMe = false,
            reposts = 5399
        ),

        Post (
            id = 3,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...",
            likes = 30,
            likeByMe = false,
            reposts = 5399
        ),

        Post (
            id = 2,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "18 сентября в 10:20",
            content = "Знаний на всех хватит, сегодня мы разбираемся с разработкой мобильных приложений",
            likes = 10,
            likeByMe = false,
            reposts = 5399
        ),

        Post (
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...",
            likes = 109,
            likeByMe = false,
            reposts = 5399
        )
    )

    private val  data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id : Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(likeByMe = !it.likeByMe, likes = if (it.likeByMe) it.likes - 1 else it.likes +1)
        }
        data.value = posts
    }

    override fun repost(id: Long) {
        posts = posts.map {
           if (it.id != id) it else it.copy (reposts = it.reposts + 1)
        }
        data.value = posts
    }

    override fun removeByID(id: Long) {
        posts = posts.filterNot { it.id == id }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts  = listOf(
                post.copy (
                author = "Me",
                likes = 0,
                likeByMe = false,
                reposts = 0,
                published = "Now" )
            ) + posts
        } else {
            posts = posts.map {
                if (it.id == post.id){
                    it.copy (content = post.content)
                } else {
                    it
                }
            }
        }
        data.value = posts
    }
}