package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

private const val BASE_URL = "http://10.0.2.2:9999"

fun CardPostBinding.bindPost(
    post: Post,
    listener: PostListener
) {
    author.text = post.author
    published.text = post.published
    content.text = post.content

    like.isChecked = post.likedByMe
    like.text = formatCount(post.likes)
    repost.text = formatCount(post.reposts)

    if (post.attachment?.type == AttachmentType.IMAGE) {
        postImage.visibility = View.VISIBLE

        val imageUrl = "$BASE_URL/media/${post.attachment.url}"

        Glide.with(postImage.context)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .timeout(30_000)
            .into(postImage)

        postImage.setOnClickListener {
            listener.onOpenImage(imageUrl)
        }
    } else {
        postImage.visibility = View.GONE
        postImage.setImageDrawable(null)
        postImage.setOnClickListener(null)
    }

    if (post.video != null) {
        videoPreview.visibility = View.VISIBLE
        videoPlay.visibility = View.VISIBLE

        val videoClickListener = View.OnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(post.video)
            )
            it.context.startActivity(intent)
        }

        videoPreview.setOnClickListener(videoClickListener)
        videoPlay.setOnClickListener(videoClickListener)
    } else {
        videoPreview.visibility = View.GONE
        videoPlay.visibility = View.GONE

        videoPreview.setOnClickListener(null)
        videoPlay.setOnClickListener(null)
    }

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

    like.setOnClickListener {
        listener.onLike(post)
    }

    repost.setOnClickListener {
        listener.onRepost(post)
    }

    root.setOnClickListener {
        listener.onOpenPost(post)
    }
}

private fun formatCount(count: Int): String = when {
    count < 1000 -> count.toString()

    count < 10_000 -> {
        val thousands = (count / 100) / 10.0
        thousands.toString().replace(".0", "") + "K"
    }

    count < 1_000_000 -> {
        "${count / 1000}K"
    }

    else -> {
        val millions = (count / 100_000) / 10.0
        millions.toString().replace(".0", "") + "M"
    }
}