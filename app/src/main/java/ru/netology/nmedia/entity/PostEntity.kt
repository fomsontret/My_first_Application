package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

    @Entity(tableName = "posts")
    data class PostEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long = 0,
        @ColumnInfo(name = "author")
        val author: String = "",
        @ColumnInfo(name = "published")
        val published: String = "",
        @ColumnInfo(name = "content")
        val content: String = "",
        @ColumnInfo(name = "likes")
        val likes: Int = 0,
        @ColumnInfo(name = "likeByMe")
        val likeByMe: Boolean = false,
        @ColumnInfo(name = "reposts")
        val reposts: Int = 0,
        @ColumnInfo(name = "video")
        val video: String? = null
    )  {
    fun toDto(): Post  = Post(
        id = id,
        author = author,
        published = published,
        content = content,
        likes = likes,
        likedByMe = likeByMe,
        reposts = reposts,
        video = video
    )
        companion object {
            fun fromDto(dto: Post): PostEntity = with(dto) {
                PostEntity(
                    id = id,
                    author = author,
                    published = published,
                    content = content,
                    likes = likes,
                    likeByMe = likedByMe,
                    reposts = reposts,
                    video = video
                )
            }
        }



}