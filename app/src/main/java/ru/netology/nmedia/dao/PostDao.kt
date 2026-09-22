package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM posts WHERE visible = 1 ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id")
    fun getById(id: Long): PostEntity?

    @Query("SELECT MAX(id) FROM posts")
    fun getMaxId(): Long?

    @Query("SELECT COUNT(*) FROM posts WHERE visible = 0")
    fun getNewPostsCount(): Int

    @Query("UPDATE posts SET visible = 1 WHERE visible = 0")
    fun showNewPosts()

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