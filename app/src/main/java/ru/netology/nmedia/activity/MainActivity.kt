package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(
            object : PostListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onRepost(post: Post) {
                    viewModel.repost(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }
            }
        )
        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { edited ->
            if (edited.id != 0L) {
                binding.editGroup.visibility = View.VISIBLE
                binding.editLabel.text = edited.content
                binding.content.setText(edited.content)
                AndroidUtils.showKeyboard(binding.content)
            } else {
                binding.editGroup.visibility = View.GONE
                binding.content.text.clear()
            }
        }

        binding.cancelEdit.setOnClickListener {
            viewModel.cancelEdit()
            binding.content.text.clear()
            AndroidUtils.hideKeyboard(binding.cancelEdit)
        }

        binding.save.setOnClickListener {
            val content = binding.content.text?.toString()

            if (content.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.error_empty_text), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.save(content)
            binding.content.clearFocus()
            binding.content.setText("")
            AndroidUtils.hideKeyboard(binding.content)
        }
    }
}