package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

private const val BASE_URL = "http://10.0.2.2:9999"

interface PostListener {
    fun onRepost(post: Post)
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onOpenPost(post: Post)
}

class PostsAdapter(
    private val listener: PostListener
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(
        viewHolder: PostViewHolder,
        position: Int
    ) {
        viewHolder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val listener: PostListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.bindPost(post, listener)

            val avatarUrl = post.authorAvatar
                ?.takeIf { it.isNotBlank() }
                ?.let { "$BASE_URL/avatars/$it" }

            Glide.with(binding.root.context)
                .load(avatarUrl)
                .placeholder(R.drawable.post_avatar_drawable)
                .error(R.drawable.post_avatar_drawable)
                .timeout(10_000)
                .transform(CircleCrop())
                .into(binding.avatar)
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ) = oldItem == newItem
}



