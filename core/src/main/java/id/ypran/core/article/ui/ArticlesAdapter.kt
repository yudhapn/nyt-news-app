package id.ypran.core.article.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.model.ArticleDiffCallback
import id.ypran.core.article.ui.ArticlesAdapter.ViewHolder
import id.ypran.core.article.ui.ArticlesAdapter.ViewHolder.Companion.from
import id.ypran.core.databinding.ItemsArticleLayoutBinding

class ArticlesAdapter(private val listener: ArticlesAdapterListener) :
    PagingDataAdapter<Article, ViewHolder>(ArticleDiffCallback) {
    interface ArticlesAdapterListener {
        fun onArticleClicked(cardView: View, article: Article, position: Int)
        fun onFavoriteClicked(article: Article)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, position)
        }
    }

    class ViewHolder private constructor(
        private val binding: ItemsArticleLayoutBinding,
        private val listener: ArticlesAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article, position: Int) {
            binding.position = position
            binding.article = article
            binding.listener = listener
        }

        companion object {
            fun from(parent: ViewGroup, listener: ArticlesAdapterListener) = ViewHolder(
                ItemsArticleLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
        }
    }
}