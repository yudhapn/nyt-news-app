package id.ypran.core.article.domain.usecase

import androidx.paging.PagingData
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteArticlesUseCase(private val repository: ArticleRepository) {
    fun execute(): Flow<PagingData<Article>> =
        repository.getFavoriteArticles()
}