package id.ypran.core.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_remote_keys_entities")
data class ArticleRemoteKeysEntity(
    @PrimaryKey
    val articleId: String,
    val prevKey: Int?,
    val nextKey: Int?
)