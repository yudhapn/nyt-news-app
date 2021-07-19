package id.ypran.search.datasource.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.ypran.core.datasource.local.entity.ArticleEntity
import id.ypran.core.datasource.remote.response.mapToEntity
import id.ypran.search.datasource.local.SearchArticlesDb
import id.ypran.search.datasource.local.entity.SearchRemoteKeysEntity
import id.ypran.search.datasource.remote.ArticleSearchApi
import retrofit2.HttpException
import java.io.IOException

private const val SEARCH_STARTING_PAGE_INDEX = 0

@OptIn(ExperimentalPagingApi::class)
class SearchArticlesRemoteMediator(
    private val articleSearchApi: ArticleSearchApi,
    private val searchArticlesDb: SearchArticlesDb,
    private val query: String,
    private val beginDate: String,
    private val endDate: String,
    private val sort: String,
    private val key: String,
) : RemoteMediator<Int, ArticleEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.APPEND -> {
                val searchRemoteKeys = getSearchRemoteKeyForLastItem(state)
                val nextKey = searchRemoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = searchRemoteKeys != null)
                nextKey
            }
            LoadType.PREPEND -> {
                val searchRemoteKeys = getSearchRemoteKeyForFirstItem(state)
                val prevKey = searchRemoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = searchRemoteKeys != null)
                prevKey
            }
            LoadType.REFRESH -> {
                val searchRemoteKeys = getSearchRemoteKeyClosestToCurrentPosition(state)
                searchRemoteKeys?.nextKey?.minus(1) ?: SEARCH_STARTING_PAGE_INDEX
            }
        }
        return try {
            val response =
                articleSearchApi.getSearchArticlesResult(
                    apiKey = key,
                    query = query,
                    beginDate = beginDate,
                    endDate = endDate,
                    page = page,
                    sort = sort
                )
            val searchResult = response.response.docs.mapToEntity()
            val isEndOfPagination = searchResult.isEmpty()
            Log.d("searchrepo", "searchResult: $searchResult")

            searchArticlesDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    searchArticlesDb.searchArticleDao.clearSearch()
                    searchArticlesDb.searchRemoteKeysDao.clearArticleRemoteKeys()
                }
                val prevKey = if (page == SEARCH_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfPagination) null else page + 1
                val keys = searchResult.map {
                    SearchRemoteKeysEntity(
                        articleSearchId = it.articleId,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                searchArticlesDb.searchArticleDao.insertAllSearchResult(searchResult)
                searchArticlesDb.searchRemoteKeysDao.insertArticleRemoteKeys(keys)
            }
            MediatorResult.Success(endOfPaginationReached = isEndOfPagination)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getSearchRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>): SearchRemoteKeysEntity? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { searchItem ->
            searchArticlesDb.searchRemoteKeysDao.remoteKeysArticleId(searchItem.articleId)
        }
    }

    private suspend fun getSearchRemoteKeyForFirstItem(state: PagingState<Int, ArticleEntity>): SearchRemoteKeysEntity? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { searchItem ->
            searchArticlesDb.searchRemoteKeysDao.remoteKeysArticleId(searchItem.articleId)
        }
    }

    private suspend fun getSearchRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ArticleEntity>): SearchRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.articleId?.let { searchItemId ->
                searchArticlesDb.searchRemoteKeysDao.remoteKeysArticleId(searchItemId)
            }
        }
    }
}