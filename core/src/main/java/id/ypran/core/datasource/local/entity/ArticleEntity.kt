package id.ypran.core.datasource.local.entity

import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.model.Multimedia

@Entity(tableName = "article_entities")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true)
    @field:SerializedName("id")
    val id: Int = 0,
    @field:SerializedName("articleId")
    val articleId: String,
    @field:SerializedName("snippet")
    val snippet: String,
    @field:SerializedName("article_abstract")
    val articleAbstract: String,
    @field:SerializedName("lead_paragraph")
    val leadParagraph: String,
    @field:SerializedName("multimedia")
    val multimedia: List<MultimediaEntity>,
    @field:SerializedName("headline")
    val headline: String,
    @field:SerializedName("byline")
    val byline: String,
    @field:SerializedName("web_url")
    val webUrl: String,
    @field:SerializedName("pub_date")
    val pubDate: String,
    @field:SerializedName("is_favorite")
    var isFavorite: Boolean = false
)

data class MultimediaEntity(
    @field:SerializedName("url")
    val url: String,
    @field:SerializedName("subType")
    val subType: String
)

fun PagingData<ArticleEntity>.mapToDomain(): PagingData<Article> = map {
    it.mapToDomain()
}

fun ArticleEntity.mapToDomain(): Article = Article(
    id = id,
    articleId = articleId,
    abstract = articleAbstract,
    snippet = snippet,
    leadParagraph = leadParagraph,
    multimedia = multimedia.mapMultimediaListToDomain(),
    headline = headline,
    byline = byline,
    webUrl = webUrl,
    pubDate = pubDate,
    isFavorite = isFavorite
)

fun List<MultimediaEntity>.mapMultimediaListToDomain(): List<Multimedia> = map {
    it.mapToDomain()
}

fun MultimediaEntity.mapToDomain(): Multimedia = Multimedia(
    url = url,
    subType = subType
)


fun Article.mapToEntity(): ArticleEntity = ArticleEntity(
    id = id,
    articleId = articleId,
    articleAbstract = abstract,
    snippet = snippet,
    leadParagraph = leadParagraph,
    multimedia = multimedia.mapMultimediaListToEntity(),
    headline = headline,
    byline = byline,
    webUrl = webUrl,
    pubDate = pubDate,
    isFavorite = isFavorite
)

fun List<Multimedia>.mapMultimediaListToEntity(): List<MultimediaEntity> = map {
    it.mapToEntity()
}

fun Multimedia.mapToEntity(): MultimediaEntity = MultimediaEntity(
    url = url,
    subType = subType
)
