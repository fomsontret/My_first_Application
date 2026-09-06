package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException

class PostRepositoryImpl : PostRepository {

    private val api = PostsApi.retrofitService

    override suspend fun getAll(): List<Post> {
        val response = api.getAll()

        if (!response.isSuccessful) {
            throw IOException(
                "Ошибка загрузки постов: ${response.code()}"
            )
        }

        return response.body() ?: emptyList()
    }

    override suspend fun likeById(id: Long): Post {
        val postResponse = api.getById(id)

        if (!postResponse.isSuccessful) {
            throw IOException(
                "Не удалось получить пост: ${postResponse.code()}"
            )
        }

        val post = postResponse.body()
            ?: throw IOException("Пост с id=$id не найден")

        val response = if (post.likedByMe) {
            api.dislikeById(id)
        } else {
            api.likeById(id)
        }

        if (!response.isSuccessful) {
            throw IOException(
                "Ошибка изменения лайка: ${response.code()}"
            )
        }

        return response.body()
            ?: throw IOException("Сервер вернул пустой ответ")
    }

    override suspend fun repost(id: Long) {
        throw UnsupportedOperationException(
            "Метод репоста не реализован"
        )
    }

    override suspend fun removeByID(id: Long) {
        val response = api.removeById(id)

        if (!response.isSuccessful) {
            throw IOException(
                "Ошибка удаления: ${response.code()}"
            )
        }
    }

    override suspend fun save(post: Post) {
        val response = api.save(post)

        if (!response.isSuccessful) {
            throw IOException(
                "Ошибка сохранения: ${response.code()}"
            )
        }
    }
}