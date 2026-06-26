package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.bindPost
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.fragment.NewPostFragment.Companion.textArg
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val postId = arguments?.textArg?.toLong() ?: return binding.root

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId } ?: return@observe

            binding.cardPost.bindPost(post, object : PostListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onRepost(post: Post) {
                    viewModel.repost(post.id)
                    val intent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, post.content)
                    }
                    startActivity(
                        android.content.Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    )
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_postFragment_to_newPostFragment,
                        Bundle().apply { textArg = post.content }
                    )
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                    findNavController().navigateUp()
                }

                override fun onOpenPost(post: Post) = Unit
            })
        }

        return binding.root
    }
}