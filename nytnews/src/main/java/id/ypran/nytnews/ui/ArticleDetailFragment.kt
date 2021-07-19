package id.ypran.nytnews.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.transition.MaterialContainerTransform
import id.ypran.core.article.domain.model.Article
import id.ypran.kozziinews.ui.MainViewModel
import id.ypran.nytnews.databinding.FragmentArticleDetailBinding
import id.ypran.nytnews.di.injectFeature
import id.ypran.nytnews.ui.ArticlesPagerAdapter.ArticlesPagerAdapterListener
import id.ypran.nytnews.util.DepthPageTransformer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.LazyThreadSafetyMode.NONE
import id.ypran.core.R as coreResource
import id.ypran.kozziinews.R as appResource

class ArticleDetailFragment : Fragment(), ArticlesPagerAdapterListener {
    private lateinit var binding: FragmentArticleDetailBinding
    private val args: ArticleDetailFragmentArgs by navArgs()
    private val position: Int by lazy(NONE) { args.position }
    private val viewModel: ArticleDetailViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = appResource.id.nav_host_fragment
            duration = resources.getInteger(appResource.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
        }
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val articlesPagerAdapter = ArticlesPagerAdapter(this)
        binding.articlesPager.setPageTransformer(DepthPageTransformer())
        binding.articlesPager.adapter = articlesPagerAdapter
        lifecycleScope.launch {
            mainViewModel.articles
                ?.onCompletion {
                    delay(50)
                    binding.articlesPager.setCurrentItem(position, false)
                }
                ?.collect {
                    articlesPagerAdapter.submitData(lifecycle, it)

                }
        }
        binding.articlesPager.registerOnPageChangeCallback(callback)
    }

    val callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            Log.d("FeedsFragment", "ArticleDetailFragment position: $position")
            mainViewModel.setPosition(position)
        }
    }

    override fun onFavoriteClicked(article: Article, imageView: View) {
        val drawable =
            if (article.isFavorite) coreResource.drawable.ic_favorite_border else coreResource.drawable.ic_favorite_red
        (imageView as ImageView).setImageResource(drawable)
        viewModel.likeArticle(article)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.articlesPager.unregisterOnPageChangeCallback(callback)
    }
}