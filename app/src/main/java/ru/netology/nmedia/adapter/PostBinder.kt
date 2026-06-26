package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

fun CardPostBinding.bindPost(post: Post, listener: PostListener) {
    author.text = post.author
    published.text = post.published
    content.text = post.content
    like.isChecked = post.likeByMe
    like.text = formatCount(post.likes)
    repost.text = formatCount(post.reposts)

    if (post.video != null) {
        videoPreview.visibility = View.VISIBLE
        videoPlay.visibility = View.VISIBLE
        val videoClickListener = View.OnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
            it.context.startActivity(intent)
        }
        videoPreview.setOnClickListener(videoClickListener)
        videoPlay.setOnClickListener(videoClickListener)
    } else {
        videoPreview.visibility = View.GONE
        videoPlay.visibility = View.GONE
    }

    menu.setOnClickListener {
        androidx.appcompat.widget.PopupMenu(it.context, it).apply {
            inflate(ru.netology.nmedia.R.menu.post_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    ru.netology.nmedia.R.id.remove -> { listener.onRemove(post); true }
                    ru.netology.nmedia.R.id.edit -> { listener.onEdit(post); true }
                    else -> false
                }
            }
            show()
        }
    }

    like.setOnClickListener { listener.onLike(post) }
    repost.setOnClickListener { listener.onRepost(post) }
    root.setOnClickListener { listener.onOpenPost(post) }
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