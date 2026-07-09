package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Upsert
    fun save(post: PostEntity)

    @Query("""
        UPDATE posts SET
        likes = likes + CASE WHEN likeByMe THEN -1 ELSE 1 END,
        likeByMe = CASE WHEN likeByMe THEN 0 ELSE 1 END 
        WHERE id = :id
    """)
    fun likeById(id: Long)

    @Query("""
        UPDATE posts SET
        reposts = reposts + 1
        WHERE id = :id
    """)
    fun repost(id: Long)

    @Query("DELETE FROM posts WHERE id = :id")
    fun removeById(id: Long)
}