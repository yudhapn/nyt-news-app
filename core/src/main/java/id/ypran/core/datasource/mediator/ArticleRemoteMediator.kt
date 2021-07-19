package id.ypran.core.datasource.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.ypran.core.datasource.local.ArticleDb
import id.ypran.core.datasource.local.entity.ArticleEntity
import id.ypran.core.datasource.local.entity.ArticleRemoteKeysEntity
import id.ypran.core.datasource.remote.ArticleApi
import id.ypran.core.datasource.remote.response.mapToEntity
import retrofit2.HttpException
import java.io.IOException

private const val ARTICLE_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val articleApi: ArticleApi,
    private val articleDb: ArticleDb,
    private val year: Int,
    private val month: Int,
    private val key: String
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.APPEND -> {
                val articleRemoteKeys = getArticleRemoteKeyForLastItem(state)
                val nextKey = articleRemoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = articleRemoteKeys != null)
                nextKey
            }
            LoadType.PREPEND -> {
                val articleRemoteKeys = getArticleRemoteKeyForFirstItem(state)
                val prevKey = articleRemoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = articleRemoteKeys != null)
                prevKey
            }
            LoadType.REFRESH -> {
                val articleRemoteKeys = getArticleRemoteKeyClosestToCurrentPosition(state)
                articleRemoteKeys?.nextKey?.minus(1) ?: ARTICLE_STARTING_PAGE_INDEX
            }
        }
        return try {
            var isEndOfPagination = false
            articleDb.withTransaction {
                val isLocalEmpty = articleDb.articleDao.isArticlesEmpty()
                Log.d("ArtRemoteMediator", "isLocalEmpty: $isLocalEmpty")
                if (isLocalEmpty != 1) {
                    isEndOfPagination = true
                } else {
                    val response = articleApi.getArticles(year, month, key)
                    val articles =
                        response.response.docs.mapToEntity()
                    isEndOfPagination = articles.isEmpty()

//            articleDb.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        articleDb.articleRemoteKeysDao.clearArticleRemoteKeys()
                        articleDb.articleDao.clearArticles()
                    }
                    val prevKey = if (page == ARTICLE_STARTING_PAGE_INDEX) null else page - 1
                    val nextKey = if (isEndOfPagination) null else page + 1
                    val keys = articles.map {
                        ArticleRemoteKeysEntity(
                            articleId = it.articleId,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                    articleDb.articleDao.insertArticles(articles)
                    articleDb.articleRemoteKeysDao.insertArticleRemoteKeys(keys)
                }
            }
            MediatorResult.Success(endOfPaginationReached = isEndOfPagination)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getArticleRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>): ArticleRemoteKeysEntity? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { article ->
            articleDb.articleRemoteKeysDao.remoteKeysArticleId(article.articleId)
        }
    }

    private suspend fun getArticleRemoteKeyForFirstItem(state: PagingState<Int, ArticleEntity>): ArticleRemoteKeysEntity? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { article ->
            articleDb.articleRemoteKeysDao.remoteKeysArticleId(article.articleId)
        }
    }

    private suspend fun getArticleRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ArticleEntity>): ArticleRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.articleId?.let { articleId ->
                articleDb.articleRemoteKeysDao.remoteKeysArticleId(articleId)
            }
        }
    }
}