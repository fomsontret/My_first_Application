package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import android.view.View

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent.getStringExtra(KEY_POST_TEXT)?.let { originalText ->
            binding.editGroup.visibility = View.VISIBLE
            binding.editLabel.text = originalText
            binding.edit.setText(originalText)
            binding.edit.setSelection(originalText.length)
        }

        binding.cancelEdit.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString()
            if (text.isBlank()) {
                setResult(RESULT_CANCELED)
            } else {
                setResult(
                    RESULT_OK,
                    Intent().apply { putExtra(KEY_POST_TEXT, text) }
                )
            }
            finish()
        }
    }

    companion object {
        const val KEY_POST_TEXT = "post_text"
    }
}

object NewPostContract : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?) =
        Intent(context, NewPostActivity::class.java).apply {
            putExtra(NewPostActivity.KEY_POST_TEXT, input)
        }

    override fun parseResult(resultCode: Int, intent: Intent?) =
        intent?.getStringExtra(NewPostActivity.KEY_POST_TEXT)
}