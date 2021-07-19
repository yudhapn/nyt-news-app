package id.ypran.search.domain.repository

import androidx.paging.PagingData
import id.ypran.core.article.domain.model.Article
import id.ypran.search.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchArticlesRepository {
    fun searchArticlesByTitle(
        histories: List<SearchHistory>,
        query: String,
        beginDate: String,
        endDate: String,
        sort: String
    ): Flow<PagingData<Article>>

    suspend fun getLatestSearchHistory(): Flow<List<SearchHistory>>
}