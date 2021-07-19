package id.ypran.core.datasource.remote.response

import com.google.gson.annotations.SerializedName
import id.ypran.core.datasource.local.entity.ArticleEntity
import id.ypran.core.datasource.local.entity.MultimediaEntity


data class ArticleListResponse(
    @SerializedName("copyright")
    val copyright: String,
    @SerializedName("response")
    val response: Response,
)

data class Response(
    @SerializedName("docs")
    val docs: List<ArticleResponse> = emptyList()
)

data class ArticleResponse(
    @SerializedName("_id")
    val articleId: String,
    @SerializedName("abstract")
    val abstract: String,
    @SerializedName("snippet")
    val snippet: String,
    @SerializedName("lead_paragraph")
    val leadParagraph: String,
    @SerializedName("multimedia")
    val multimedia: List<MultimediaResponse>,
    @SerializedName("headline")
    val headline: HeadlineResponse,
    @SerializedName("byline")
    val byline: BylineResponse?,
    @SerializedName("web_url")
    val webUrl: String,
    @SerializedName("pub_date")
    val pubDate: String
)

data class BylineResponse(
    @SerializedName("original")
    val original: String
)

data class HeadlineResponse(
    @SerializedName("main")
    val main: String
)

data class MultimediaResponse(
    @SerializedName("url")
    val url: String,
    @SerializedName("subType")
    val subType: String
)

fun List<ArticleResponse>.mapToEntity(): List<ArticleEntity> = map {
    it.mapToEntity()
}

fun ArticleResponse.mapToEntity(): ArticleEntity = ArticleEntity(
    articleId = articleId,
    articleAbstract = abstract,
    snippet = snippet,
    leadParagraph = leadParagraph,
    multimedia = multimedia.mapMultimediaListToEntity(),
    headline = headline.main,
    byline = byline?.original ?: "",
    webUrl = webUrl,
    pubDate = pubDate
)

fun List<MultimediaResponse>.mapMultimediaListToEntity(): List<MultimediaEntity> = map {
    it.mapToEntity()
}

fun MultimediaResponse.mapToEntity(): MultimediaEntity = MultimediaEntity(
    url = url,
    subType = subType
)

