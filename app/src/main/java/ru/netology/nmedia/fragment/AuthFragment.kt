package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAuthBinding.bind(view)

        binding.signIn.setOnClickListener {
            viewModel.login(
                login = binding.login.text.toString(),
                pass = binding.password.text.toString()
            )
        }

        binding.password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.login(
                    login = binding.login.text.toString(),
                    pass = binding.password.text.toString()
                )
                true
            } else {
                false
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progress.isVisible = loading
            binding.signIn.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_LONG
                ).show()

                viewModel.consumeError()
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success) {
                viewModel.consumeSuccess()
                findNavController().popBackStack()
            }
        }
    }
}