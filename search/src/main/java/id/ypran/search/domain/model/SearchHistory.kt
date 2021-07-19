package id.ypran.search.domain.model

import androidx.recyclerview.widget.DiffUtil
import java.util.Date
import java.util.Calendar

data class SearchHistory(
    val id: Int,
    val keyword: String,
    val executeAt: Date = Calendar.getInstance().time
)

object SearchHistoryDiffCallback : DiffUtil.ItemCallback<SearchHistory>() {
    override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean =
        oldItem == newItem

}