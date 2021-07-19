package id.ypran.core.article.domain.repository

import androidx.paging.PagingData
import id.ypran.core.article.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticles(month: Int, year: Int): Flow<PagingData<Article>>
    fun getFavoriteArticles(): Flow<PagingData<Article>>
    suspend fun likeArticle(article: Article)
}