package id.ypran.favorite.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.ui.ArticlesAdapter
import id.ypran.core.article.ui.ArticlesAdapter.ArticlesAdapterListener
import id.ypran.core.article.ui.ArticlesLoadStateAdapter
import id.ypran.favorite.databinding.FragmentFavoriteBinding
import id.ypran.favorite.di.injectFeature
import id.ypran.kozziinews.ui.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import id.ypran.kozziinews.R as appSource

class FavoriteFragment : Fragment(), ArticlesAdapterListener {
    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(appSource.integer.motion_duration_large).toLong()
        }
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        initView()
        initAdapter()
    }

    private fun initView() {
        binding.navigateUpButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun initAdapter() {
        val articlesAdapter = ArticlesAdapter(this)
        binding.favoriteRecyclerView.adapter =
            articlesAdapter.withLoadStateFooter(ArticlesLoadStateAdapter { articlesAdapter.retry() })
        articlesAdapter.addLoadStateListener { loadState ->
            binding.favoriteProgressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            val errorState =
                loadState.mediator?.append as? LoadState.Error
                    ?: loadState.mediator?.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error

            errorState?.let {
                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launch {
            viewModel.favoriteArticles?.collectLatest {
                mainViewModel.setArticles(it)
                articlesAdapter.submitData(lifecycle, it)
                Log.d("FavoriteFragment", "size favorite: ${articlesAdapter.itemCount}")
            }
        }
    }

    override fun onArticleClicked(cardView: View, article: Article, position: Int) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(appSource.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(appSource.integer.motion_duration_large).toLong()
        }

        val articleCardDetailTransitionName =
            getString(appSource.string.article_detail_card_transition_name)
        val extras = FragmentNavigatorExtras(cardView to articleCardDetailTransitionName)
        val directions = FavoriteFragmentDirections.actionToArticleDetailFragment(position)
        findNavController().navigate(directions, extras)
    }

    override fun onFavoriteClicked(article: Article) = viewModel.likeArticle(article)
}
