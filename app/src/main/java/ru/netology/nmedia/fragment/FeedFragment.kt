package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.add(Menu.NONE, MENU_SIGN_IN, Menu.NONE, "Sign in")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_SIGN_IN -> {
                findNavController().navigate(
                    R.id.action_feedFragment_to_authFragment
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by viewModels(::requireParentFragment)

        // Кнопка авторизации на экране ленты
        binding.signIn.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_authFragment
            )
        }

        val adapter = PostsAdapter(
            object : PostListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onRepost(post: Post) {
                    viewModel.repost(post.id)

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.content)
                    }

                    val chooser = Intent.createChooser(
                        intent,
                        getString(R.string.chooser_share_post)
                    )
                    startActivity(chooser)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        }
                    )
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onOpenPost(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_postFragment,
                        Bundle().apply {
                            textArg = post.id.toString()
                        }
                    )
                }

                override fun onOpenImage(url: String) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_photoFragment,
                        Bundle().apply {
                            putString("url", url)
                        }
                    )
                }
            }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.emptyText.isVisible = state.empty
            binding.errorGroup.isVisible = state.error
        }

        binding.retry.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.add.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment
            )
        }

        return binding.root
    }

    companion object {
        private const val MENU_SIGN_IN = 1
        var Bundle.textArg by StringArg
    }
}