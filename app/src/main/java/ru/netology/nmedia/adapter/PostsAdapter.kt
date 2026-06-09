package ru.netology.nmedia.adapter

import android.app.ProgressDialog.show
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface PostListener {
    fun onRepost (post: Post)
    fun onLike (post: Post)
    fun onEdit (post: Post)
    fun onRemove (post: Post)
}

class PostsAdapter(
    private val listener: PostListener

) : ListAdapter <Post, PostsAdapter.PostViewHolder> (PostDiffCallback) {

    private fun formatCount(count: Int): String = when {
        count < 1000 -> count.toString()
        count < 10_000 -> {
            val thousands = (count / 100) / 10.0
            thousands.toString().replace(".0", "") + "K"
        }
        count < 1_000_000 -> "${count / 1000}K"
        else -> {
            val millions = (count / 100_000) / 10.0
            millions.toString().replace(".0", "") + "M"
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int) {
        val post = getItem (position)
        viewHolder.bind(post)
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val listener: PostListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                // Здесь применяем форматирование
                likeCount.text = formatCount(post.likes)
                repostCount.text = formatCount(post.reposts)
                like.setImageResource(
                    if (post.likeByMe) R.drawable.ic_hart_red else R.drawable.ic_hart
                )

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.post_menu)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    listener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    listener.onEdit(post)
                                    true
                                }
                                else -> false
                            }
                        }
                        show()
                    }
                }

                like.setOnClickListener { listener.onLike(post) }
                repost.setOnClickListener { listener.onRepost(post) }
            }
        }

        private fun formatCount(count: Int): String = when {
            count < 1000 -> count.toString()
            count < 10_000 -> {
                val thousands = (count / 100) / 10.0
                thousands.toString().replace(".0", "") + "K"
            }
            count < 1_000_000 -> "${count / 1000}K"
            else -> {
                val millions = (count / 100_000) / 10.0
                millions.toString().replace(".0", "") + "M"
            }
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) =
        oldItem == newItem
}