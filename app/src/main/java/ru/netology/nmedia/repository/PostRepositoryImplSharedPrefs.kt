package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(private val context: Context) : PostRepository {

    private var posts = emptyList<Post>()
        set(value) {
            field = value
            sync()
        }
    private var nextId = 1L
    private val  data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    init {
        val file = context.filesDir.resolve(FILENAME_POSTS)
        if (file.exists()) {
            context.openFileInput(FILENAME_POSTS).bufferedReader().use{ str ->
                posts = gson.fromJson(str, typeToken)
                nextId = posts.maxOf { it.id } + 1
                data.value = posts
            }
        }
    }
    private fun sync() {
        context.openFileOutput(FILENAME_POSTS, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write( gson.toJson(posts))
        }
    }

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
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likes = 0,
                    likeByMe = false,
                    reposts = 0,
                    published = "Now"
                )
            ) + posts
        } else {
            posts = posts.map {
                if (it.id == post.id) it.copy(content = post.content) else it
            }
        }
        data.value = posts
    }

    companion object {
        private const val FILENAME_POSTS = "posts.json"

        private  val gson = Gson()
        private val typeToken = TypeToken.getParameterized(List::class.java, Post::class.java).type
    }

}