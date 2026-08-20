package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        val jsonType ="application/json".toMediaType()
        val gson = Gson()
        val postType = object : TypeToken<List<Post>>() {}.type
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()


    override fun getAll() : List<Post>{
        val call = client.newCall(
            Request.Builder()
                .url("$BASE_URL/api/slow/posts")
                .build()
        )
        val response = call.execute()

        val responseText =  response.body.string()

        return gson.fromJson(responseText, postType)
    }

    override fun likeById(id: Long): Post {
        val posts = getAll()
        val post = posts.first { it.id == id }
        val request = if (post.likedByMe) {
            Request.Builder()
                .url("$BASE_URL/api/slow/posts/$id/likes")
                .delete()
                .build()
        } else {
            Request.Builder()
                .url("$BASE_URL/api/slow/posts/$id/likes")
                .post(RequestBody.EMPTY)
                .build()
        }
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Unexpected code $response")
        }
        val responseText = response.body?.string() ?: ""
        return if (responseText.isNotEmpty()) {
            gson.fromJson(responseText, Post::class.java)
        } else {
            post.copy(
                likedByMe = !post.likedByMe,
                likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
            )
        }
    }

    override fun repost(id: Long) {
        //dao.repost(id)
    }

    override fun removeByID(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }


    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}