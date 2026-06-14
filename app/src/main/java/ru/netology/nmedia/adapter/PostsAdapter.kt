package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface PostListener {
    fun onRepost(post: Post)
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
}

class PostsAdapter(
    private val listener: PostListener
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
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
                like.isChecked = post.likeByMe
                like.text = formatCount(post.likes)
                repost.text = formatCount(post.reposts)

                if (post.video != null) {
                    videoPreview.visibility = View.VISIBLE
                    videoPlay.visibility = View.VISIBLE
                } else {
                    videoPreview.visibility = View.GONE
                    videoPlay.visibility = View.GONE
                }

                val videoClickListener = View.OnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    it.context.startActivity(intent)
                }

                videoPreview.setOnClickListener(videoClickListener)
                videoPlay.setOnClickListener(videoClickListener)

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
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}