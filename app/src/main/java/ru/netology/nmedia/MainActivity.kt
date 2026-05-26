package ru.netology.nmedia

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Repost

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

        fun formatCount(count: Int): String {
            return when {
                count < 1000 -> count.toString()
                count < 10_000 -> {
                    val thousands = count / 1000.0
                    String.format("%.1fK", thousands).replace(".0K", "K")
                }

                count < 1_000_000 -> {
                    val thousands = count / 1000
                    "${thousands}K"
                }

                else -> {
                    val millions = count / 1_000_000.0
                    String.format("%.1fM", millions).replace(".0M", "M")
                }
            }
        }

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология!...",
            likes = 100,
            likeByMe = false
        )

        val repostim = Repost(reposts = 10000000)

        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likeCount.text = formatCount(post.likes)
            repostCount.text = formatCount(repostim.reposts)

            like.setImageResource(if (post.likeByMe) R.drawable.ic_hart_red else R.drawable.ic_hart)

            like.setOnClickListener {
                if (post.likeByMe) post.likes-- else post.likes++
                post.likeByMe = !post.likeByMe
                like.setImageResource(if (post.likeByMe) R.drawable.ic_hart_red else R.drawable.ic_hart)
                likeCount.text = formatCount(post.likes)
            }

            repost.setOnClickListener {
                repostim.reposts++
                repostCount.text = formatCount(repostim.reposts)
            }
        }
    }

}