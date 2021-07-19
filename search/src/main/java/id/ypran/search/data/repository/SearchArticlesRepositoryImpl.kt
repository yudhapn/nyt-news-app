package id.ypran.search.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.ypran.core.article.domain.model.Article
import id.ypran.core.datasource.local.entity.mapToDomain
import id.ypran.search.datasource.local.SearchArticlesDb
import id.ypran.search.datasource.local.entity.SearchHistoryEntity
import id.ypran.search.datasource.local.entity.mapToDomain
import id.ypran.search.datasource.local.entity.mapToEntity
import id.ypran.search.datasource.mediator.SearchArticlesRemoteMediator
import id.ypran.search.domain.model.SearchHistory
import id.ypran.search.domain.repository.SearchArticlesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

private const val NETWORK_PAGE_SIZE = 8

class SearchArticlesRepositoryImpl(
    private val searchArticlesDb: SearchArticlesDb
) : SearchArticlesRepository, KoinComponent {

    @OptIn(ExperimentalPagingApi::class)
    override fun searchArticlesByTitle(
        histories: List<SearchHistory>,
        query: String,
        beginDate: String,
        endDate: String,
        sort: String
    ): Flow<PagingData<Article>> {
        insertSearchHistory(histories, query)
        val articlesRemoteMediator: SearchArticlesRemoteMediator by inject {
            parametersOf(
                query,
                beginDate,
                endDate,
                sort
            )
        }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = articlesRemoteMediator,
            pagingSourceFactory = {
                searchArticlesDb.searchArticleDao.searchArticleByTitle(
                    "%$query%"
                )
            }
        ).flow
            .map {
                it.mapToDomain()
            }
    }

    private fun insertSearchHistory(histories: List<SearchHistory>, query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var mutableHistories = histories.mapToEntity().toMutableList()
            val existingHistory = histories.find { it.keyword.equals(query, true) }
            val searchHistoryEntity = SearchHistoryEntity(keyword = query)
            if (existingHistory != null) {
                mutableHistories =
                    mutableHistories.filter { !it.keyword.equals(query, true) }.toMutableList()
            }
            mutableHistories.add(searchHistoryEntity)

            val result = searchArticlesDb.searchHistoryDao.isHistoryExists(query)
            if (result == null) {
                searchArticlesDb.searchHistoryDao.insertSearchHistory(mutableHistories)
            } else {
                Log.d("SearchRepo", "$result")
            }
        }
    }

    override suspend fun getLatestSearchHistory(): Flow<List<SearchHistory>> =
        flow {
            emit(searchArticlesDb.searchHistoryDao.getLatestHistory().mapToDomain())
        }

}
