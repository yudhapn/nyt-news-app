package id.ypran.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.search.databinding.ItemsSearchHistoryBinding
import id.ypran.search.domain.model.SearchHistory
import id.ypran.search.domain.model.SearchHistoryDiffCallback
import id.ypran.search.ui.SearchHistoriesAdapter.ViewHolder
import id.ypran.search.ui.SearchHistoriesAdapter.ViewHolder.Companion.from

class SearchHistoriesAdapter(private val listener: SearchHistoriesAdapterListener) :
    ListAdapter<SearchHistory, ViewHolder>(SearchHistoryDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    interface SearchHistoriesAdapterListener {
        fun onHistoryClicked(keyword: String)
    }

    class ViewHolder(
        private val binding: ItemsSearchHistoryBinding,
        private val listener: SearchHistoriesAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(searchHistory: SearchHistory) {
            binding.keyword = searchHistory.keyword
            binding.listener = listener
        }

        companion object {
            fun from(parent: ViewGroup, listener: SearchHistoriesAdapterListener) = ViewHolder(
                ItemsSearchHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
        }
    }
}