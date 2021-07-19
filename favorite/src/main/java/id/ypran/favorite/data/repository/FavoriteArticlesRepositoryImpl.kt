package id.ypran.favorite.data.repository

import androidx.paging.*
import id.ypran.core.article.domain.model.Article
import id.ypran.core.datasource.local.entity.mapToDomain
import id.ypran.favorite.datasource.local.FavoriteArticlesDb
import id.ypran.favorite.domain.repository.FavoriteArticlesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent

private const val NETWORK_PAGE_SIZE = 8

class FavoriteArticlesRepositoryImpl(
    private val favoriteArticlesDb: FavoriteArticlesDb
) : FavoriteArticlesRepository, KoinComponent {

    @OptIn(ExperimentalPagingApi::class)
    override fun getFavoriteArticles(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                favoriteArticlesDb.favoriteArticleDao.getFavoriteArticles()
            }
        ).flow
            .map {
                it.mapToDomain()
            }
    }
}
