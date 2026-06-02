package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

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


        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                content.text = post.content
                published.text = post.published
                likeCount.text = formatCount(post.likes)
                repostCount.text = formatCount(post.reposts)

                like.setImageResource(if (post.likeByMe) R.drawable.ic_hart_red else R.drawable.ic_hart)
            }
        }

        binding.like.setOnClickListener {
            viewModel.like()
        }

        binding.repost.setOnClickListener {
            viewModel.repost()
        }
    }
}