package ru.netology.nmedia

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + v.paddingLeft,
                systemBars.top + v.paddingTop,
                systemBars.right + v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }

        binding.root.setOnClickListener {
            println("DEBUG: root clicked")  // Точка останова здесь
        }

        binding.like.setOnClickListener {
            println("DEBUG: like clicked")  // Точка останова здесь
        }

        binding.avatar.setOnClickListener {
            println("DEBUG: avatar clicked")
        }

        fun formatCount(count: Int): String = when {
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

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...",
            likes = 100,
            likeByMe = false,
            reposts = 5399
        )

        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likeCount.text = formatCount(post.likes)
            repostCount.text = formatCount(post.reposts)

            like.setImageResource(if (post.likeByMe) R.drawable.ic_hart_red else R.drawable.ic_hart)

            like.setOnClickListener {
                if (post.likeByMe) post.likes-- else post.likes++
                post.likeByMe = !post.likeByMe
                like.setImageResource(if (post.likeByMe) R.drawable.ic_hart_red else R.drawable.ic_hart)
                likeCount.text = formatCount(post.likes)
            }

            repost.setOnClickListener {
                post.reposts++
                repostCount.text = formatCount(post.reposts)
            }
        }
    }

}