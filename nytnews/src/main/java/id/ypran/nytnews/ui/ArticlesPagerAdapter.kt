package id.ypran.nytnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.model.ArticleDiffCallback
import id.ypran.nytnews.databinding.ItemsArticleDetailLayoutBinding
import id.ypran.nytnews.ui.ArticlesPagerAdapter.ViewHolder
import id.ypran.nytnews.ui.ArticlesPagerAdapter.ViewHolder.Companion.from

class ArticlesPagerAdapter(private val listener: ArticlesPagerAdapterListener) :
    PagingDataAdapter<Article, ViewHolder>(ArticleDiffCallback) {
    interface ArticlesPagerAdapterListener {
        fun onFavoriteClicked(article: Article, imageView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class ViewHolder private constructor(
        private val binding: ItemsArticleDetailLayoutBinding,
        private val listener: ArticlesPagerAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.article = article
            binding.listener = listener
            val adapter = ImagePagerAdapter()
            binding.articleImageViewPager.adapter = adapter
            adapter.submitList(article.multimedia)
        }

        companion object {
            fun from(parent: ViewGroup, listener: ArticlesPagerAdapterListener) =
                ViewHolder(
                    ItemsArticleDetailLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    listener
                )
        }
    }

}