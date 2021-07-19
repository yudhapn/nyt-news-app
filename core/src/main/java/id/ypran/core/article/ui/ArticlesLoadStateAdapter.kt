package id.ypran.core.article.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.core.databinding.ItemsArticlesLoadStateFooterBinding

class ArticlesLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ArticlesLoadStateAdapter.ViewHolder>() {
    override fun onBindViewHolder(
        holder: ViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ViewHolder = ViewHolder.from(parent, retry)

    class ViewHolder(
        private val binding: ItemsArticlesLoadStateFooterBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                articlesProgressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
            }
        }

        companion object {
            fun from(parent: ViewGroup, retry: () -> Unit) =
                ViewHolder(
                    ItemsArticlesLoadStateFooterBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    retry
                )
        }
    }
}