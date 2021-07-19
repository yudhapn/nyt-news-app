package id.ypran.core.article.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: Int,
    val articleId: String,
    val abstract: String,
    val snippet: String,
    val leadParagraph: String,
    val multimedia: List<Multimedia>,
    val headline: String,
    val byline: String,
    val webUrl: String,
    val pubDate: String,
    var isFavorite: Boolean
) : Parcelable

@Parcelize
data class Multimedia(
    val url: String,
    val subType: String
) : Parcelable

object ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
        oldItem == newItem
}

object MultimediaDiffCallback : DiffUtil.ItemCallback<Multimedia>() {
    override fun areItemsTheSame(oldItem: Multimedia, newItem: Multimedia): Boolean =
        oldItem.url == newItem.url

    override fun areContentsTheSame(oldItem: Multimedia, newItem: Multimedia): Boolean =
        oldItem == newItem
}