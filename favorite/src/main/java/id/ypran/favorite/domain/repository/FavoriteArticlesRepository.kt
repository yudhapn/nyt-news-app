package id.ypran.favorite.domain.repository

import androidx.paging.PagingData
import id.ypran.core.article.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface FavoriteArticlesRepository {
    fun getFavoriteArticles(): Flow<PagingData<Article>>
}