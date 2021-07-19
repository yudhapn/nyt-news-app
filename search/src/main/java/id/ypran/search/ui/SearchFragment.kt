package id.ypran.search.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.ui.ArticlesAdapter
import id.ypran.core.article.ui.ArticlesAdapter.ArticlesAdapterListener
import id.ypran.core.article.ui.ArticlesLoadStateAdapter
import id.ypran.core.util.hideSoftKeyboard
import id.ypran.core.util.showSoftKeyboard
import id.ypran.kozziinews.ui.FeedsFragmentDirections
import id.ypran.kozziinews.ui.MainViewModel
import id.ypran.search.databinding.FragmentSearchBinding
import id.ypran.search.di.injectFeature
import id.ypran.search.ui.SearchHistoriesAdapter.SearchHistoriesAdapterListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import id.ypran.kozziinews.R as appSource

class SearchFragment : Fragment(), ArticlesAdapterListener, SearchHistoriesAdapterListener {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private var searchJob: Job? = null
    private val articlesAdapter = ArticlesAdapter(this)
    private var query: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(appSource.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(appSource.integer.motion_duration_large).toLong()
        }
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        initView()
        initHistoriesAdapter()
        initSearchAdapter()
    }

    private fun initHistoriesAdapter() {
        val historiesAdapter = SearchHistoriesAdapter(this)
        binding.searchHistoryRecyclerView.adapter = historiesAdapter
        viewModel.searchHistories.observe(viewLifecycleOwner) {
            Log.d("SearchFragment", "histories size: ${it.size}")
            historiesAdapter.submitList(it)
        }
    }

    private fun initSearchAdapter() {
        binding.searchRecyclerView.adapter =
            articlesAdapter.withLoadStateFooter(ArticlesLoadStateAdapter { articlesAdapter.retry() })
        articlesAdapter.addLoadStateListener { loadState ->
            binding.searchRecyclerView.isVisible =
                loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
            binding.searchProgressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            val isListEmpty =
                query.isNotEmpty() && loadState.refresh is LoadState.NotLoading && articlesAdapter.itemCount == 0
            showEmptyList(isListEmpty)
            val errorState =
                loadState.mediator?.append as? LoadState.Error
                    ?: loadState.mediator?.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error

            errorState?.let {
                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showEmptyList(isEmpty: Boolean) {
        binding.emptyResultImageView.isVisible = isEmpty
    }

    private fun initView() {
        with(binding) {
            searchEditText.requestFocus()
            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                binding.searchHistoryRecyclerView.isVisible = hasFocus
            }
            if (articlesAdapter.itemCount == 0) {
                requireContext().showSoftKeyboard(searchEditText)
            }

            navigateUpButton.setOnClickListener { findNavController().navigateUp() }
            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    query = searchEditText.text.toString()
                    search(query)
                    requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
                    handled = true
                }
                handled
            }
            setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(
                        names: List<String>, sharedElements: MutableMap<String, View>
                    ) {
                        mainViewModel.position.observe(viewLifecycleOwner) { position ->
                            val selectedViewHolder =
                                searchRecyclerView
                                    .findViewHolderForAdapterPosition(position) as ArticlesAdapter.ViewHolder?
                            if (selectedViewHolder != null) {
                                sharedElements[names[0]] = selectedViewHolder.itemView
                            }
                        }
                    }
                })

        }
    }

    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchCatalogue(query).collectLatest {
                mainViewModel.setArticles(it)
                articlesAdapter.submitData(lifecycle, it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
    }

    override fun onArticleClicked(cardView: View, article: Article, position: Int) {
        mainViewModel.setPosition(position)

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(appSource.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(appSource.integer.motion_duration_large).toLong()
        }

        val articleCardDetailTransitionName =
            getString(appSource.string.article_detail_card_transition_name)
        val extras = FragmentNavigatorExtras(cardView to articleCardDetailTransitionName)
        val directions = FeedsFragmentDirections.actionToArticleDetailFragment(position)
        findNavController().navigate(directions, extras)
    }

    override fun onFavoriteClicked(article: Article) {

    }

    override fun onHistoryClicked(keyword: String) {
        with(binding) {
            searchEditText.setText(keyword)
            searchHistoryRecyclerView.isVisible = false
            requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
        }
        search(keyword)
    }
}
