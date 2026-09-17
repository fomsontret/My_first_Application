package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by viewModels(::requireParentFragment)

        AndroidUtils.showKeyboard(binding.edit)

        arguments?.textArg?.let { text ->
            binding.edit.setText(text)
            binding.editGroup.visibility = View.VISIBLE
            binding.editLabel.text = text
        }

        binding.cancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ok.setOnClickListener {
            if (!binding.edit.text.isNullOrBlank()) {
                viewModel.save(binding.edit.text.toString())
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                "Не удалось сохранить пост",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Повторить") {
                viewModel.save(binding.edit.text.toString())
            }.show()
        }

        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }
}