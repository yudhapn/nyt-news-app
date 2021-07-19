package id.ypran.core.article.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.repository.ArticleRepository
import id.ypran.core.datasource.local.ArticleDb
import id.ypran.core.datasource.local.entity.FavoriteArticleEntity
import id.ypran.core.datasource.local.entity.mapToDomain
import id.ypran.core.datasource.local.entity.mapToEntity
import id.ypran.core.datasource.mediator.ArticleRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

private const val NETWORK_PAGE_SIZE = 10

@OptIn(ExperimentalPagingApi::class)
class ArticleRepositoryImpl(
    private val articleDb: ArticleDb
) : ArticleRepository, KoinComponent {

    override fun getArticles(month: Int, year: Int): Flow<PagingData<Article>> {
        val articleRemoteMediator: ArticleRemoteMediator by inject { parametersOf(month, year) }

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = articleRemoteMediator,
            pagingSourceFactory = { articleDb.articleDao.getArticles() }
        ).flow
            .map {
                Log.d("ArticleRepo", "getArticles")
                it.mapToDomain()
            }
    }

    override fun getFavoriteArticles(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { articleDb.articleDao.getFavoriteArticlesPaging() }
        ).flow
            .map {
                Log.d("ArticleRepo", "getFavoriteArticles called")
                it.mapToDomain()
            }
    }

    override suspend fun likeArticle(article: Article) {
        val articleEntity = article.mapToEntity()
        Log.d("ArticleRepo", "articleId: ${articleEntity.id}")
        val favoriteArticle = articleDb.articleDao.getFavoriteArticle(article.articleId)
        if (favoriteArticle == null) {
            articleEntity.isFavorite = true
            val favorite = FavoriteArticleEntity(article = articleEntity)
            articleDb.articleDao.insertFavoriteArticles(favorite)
        } else {
            articleEntity.isFavorite = false
            articleDb.articleDao.deleteFavoriteArticles(article.articleId)
        }
        articleDb.articleDao.updateArticle(articleEntity)
    }
}