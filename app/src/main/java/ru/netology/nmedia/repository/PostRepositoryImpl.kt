package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException

class PostRepositoryImpl(
    private val db: AppDb
) : PostRepository {

    private val api = PostsApi.retrofitService
    private val dao = db.postDao

    override suspend fun getAll(): List<Post> {
        val response = api.getAll()

        if (!response.isSuccessful) {
            throw IOException(
                "Ошибка загрузки постов: ${response.code()}"
            )
        }

        val posts = response.body() ?: emptyList()

        posts.forEach { post ->
            dao.save(
                PostEntity.fromDto(post, visible = true)
            )
        }

        return posts
    }

    override suspend fun getNewer(): Int {
        val lastId = dao.getMaxId() ?: 0

        val response = api.getNewer(lastId)

        if (!response.isSuccessful) {
            throw IOException(
                "Ошибка загрузки новых постов: ${response.code()}"
            )
        }

        val posts = response.body() ?: emptyList()

        posts.forEach { post ->
            dao.save(
                PostEntity.fromDto(
                    post,
                    visible = false
                )
            )
        }

        return dao.getNewPostsCount()
    }

    override suspend fun getNewPostsCount(): Int {
        return dao.getNewPostsCount()
    }

    override suspend fun showNewPosts() {
        dao.showNewPosts()
    }

    override suspend fun likeById(id: Long): Post {
        val post = dao.getById(id)
            ?: throw IOException("Пост с id=$id не найден")

        dao.likeById(id)

        try {
            val response = if (post.likeByMe) {
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

        } catch (e: IOException) {
            dao.save(post)
            throw e
        }
    }

    override suspend fun repost(id: Long) {
        throw UnsupportedOperationException(
            "Метод репоста не реализован"
        )
    }

    override suspend fun removeById(id: Long) {
        val post = dao.getById(id)
            ?: throw IOException("Пост с id=$id не найден")

        dao.removeById(id)

        try {
            val response = api.removeById(id)

            if (!response.isSuccessful) {
                throw IOException(
                    "Ошибка удаления: ${response.code()}"
                )
            }
        } catch (e: IOException) {
            dao.save(post)
            throw e
        }
    }

    override suspend fun save(post: Post) {
        val response = api.save(post)

        if (!response.isSuccessful) {
            throw IOException(
                "Ошибка сохранения: ${response.code()}"
            )
        }

        response.body()?.let {
            dao.save(
                PostEntity.fromDto(it, visible = true)
            )
        }
    }
}