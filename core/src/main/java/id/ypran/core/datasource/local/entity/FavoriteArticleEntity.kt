package id.ypran.core.datasource.local.entity

import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import id.ypran.core.article.domain.model.Article

@Entity(tableName = "favorite_article_entity")
data class FavoriteArticleEntity(
    @PrimaryKey(autoGenerate = true)
    @field:SerializedName("id")
    val favoriteId: Int = 0,
    @Embedded
    val article: ArticleEntity
)

fun PagingData<FavoriteArticleEntity>.mapToDomain(): PagingData<Article> = map {
    it.article.mapToDomain()
}