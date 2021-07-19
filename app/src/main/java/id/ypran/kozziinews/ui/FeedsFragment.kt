package id.ypran.kozziinews.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.SharedElementCallback
import androidx.core.content.edit
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.ui.ArticlesAdapter
import id.ypran.core.article.ui.ArticlesAdapter.ArticlesAdapterListener
import id.ypran.core.article.ui.ArticlesLoadStateAdapter
import id.ypran.kozziinews.R
import id.ypran.kozziinews.databinding.FragmentFeedsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FeedsFragment : Fragment(), ArticlesAdapterListener {
    private lateinit var binding: FragmentFeedsBinding
    private val viewModel: FeedsViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private lateinit var shared: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        Log.d("FeedsFragment", "onCreate")
        shared =
            requireContext().getSharedPreferences("user_preference", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        initAdapter()
        initView()
    }

    private fun setThemeMode(): Int {
        val isDarkMode = shared.getInt("THEME_MODE", 0) == MODE_NIGHT_YES
        Log.d("FeedsFragment", "THEME_MODE value ${shared.getInt("THEME_MODE", 0)}")
//            binding.themeModeMenuButton.drawable.constantState == ResourcesCompat.getDrawable(
//                resources,
//                R.drawable.ic_dark_mode,
//                requireContext().theme
//            )?.constantState
        var themeMode: Int = MODE_NIGHT_YES
        var themeModeImageRes: Int = R.drawable.ic_light_mode
        if (isDarkMode) {
            themeModeImageRes = R.drawable.ic_dark_mode
            themeMode = MODE_NIGHT_NO
            Log.d("FeedsFragment", "setThemeMode mode dark turn off $themeMode")
        } else {
            Log.d("FeedsFragment", "setThemeMode mode dark turn on $themeMode")
        }
        binding.themeModeMenuButton.setImageResource(themeModeImageRes)
        return themeMode
//        mainViewModel.themeMode.observe(viewLifecycleOwner) {
//            val imageResource: Int = if (it == AppCompatDelegate.MODE_NIGHT_NO) {
//                Log.d("FeedsFragment", "mode dark night no: ${AppCompatDelegate.MODE_NIGHT_NO}")
//                R.drawable.ic_light_mode
//            } else {
//                Log.d("FeedsFragment", "mode dark night yes: ${AppCompatDelegate.MODE_NIGHT_YES}")
//                R.drawable.ic_dark_mode
//            }
//            binding.themeModeMenuButton.setImageResource(imageResource)
//        }
    }


    private fun initView() {
        setThemeMode()
        binding.searchMenuButton.setOnClickListener { navigateToSearch() }
        binding.favoriteMenuButton.setOnClickListener { navigateToFavorite() }
        binding.themeModeMenuButton.setOnClickListener {
            val themeMode = setThemeMode()
            Log.d("FeedsFragment", "onclick mode dark $themeMode")
            if (themeMode != 0) (requireActivity() as HomeActivity).delegate.localNightMode =
                themeMode
            shared.edit {
                putInt("THEME_MODE", themeMode)
                apply()
            }
//            mainViewModel.setThemeMode(themeMode)
        }
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>, sharedElements: MutableMap<String, View>
                ) {
                    mainViewModel.position.observe(viewLifecycleOwner) { position ->
                        val selectedViewHolder =
                            binding.articlesRecyclerView
                                .findViewHolderForAdapterPosition(position) as ArticlesAdapter.ViewHolder?
                        if (selectedViewHolder != null) {
                            sharedElements[names[0]] = selectedViewHolder.itemView
                        }
                    }
                }
            })
    }

    private fun initAdapter() {
        val articlesAdapter = ArticlesAdapter(this)
        binding.articlesRecyclerView.adapter =
            articlesAdapter.withLoadStateFooter(ArticlesLoadStateAdapter { articlesAdapter.retry() })
        articlesAdapter.addLoadStateListener { loadState ->
//            binding.articlesRecyclerView.isVisible =
//                loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
            binding.articlesProgressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
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
            viewModel.articles?.collectLatest {
                mainViewModel.setArticles(it)
                articlesAdapter.submitData(lifecycle, it)
            }
        }
    }

    override fun onArticleClicked(cardView: View, article: Article, position: Int) {
        mainViewModel.setPosition(position)

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }

        val articleCardDetailTransitionName =
            getString(R.string.article_detail_card_transition_name)
        val extras = FragmentNavigatorExtras(cardView to articleCardDetailTransitionName)
        val directions = FeedsFragmentDirections.actionToArticleDetailFragment(position)
        findNavController().navigate(directions, extras)
    }

    override fun onFavoriteClicked(article: Article) = viewModel.likeArticle(article)

    private fun navigateToSearch() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        val directions = FeedsFragmentDirections.actionToSearchFragment()
        findNavController().navigate(directions)
    }

    private fun navigateToFavorite() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        val directions = FeedsFragmentDirections.actionToFavoriteFragment()
        findNavController().navigate(directions)
    }
}