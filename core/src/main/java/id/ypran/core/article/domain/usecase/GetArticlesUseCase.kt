package id.ypran.core.article.domain.usecase

import androidx.paging.PagingData
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetArticlesUseCase(private val repository: ArticleRepository) {
    fun execute(month: Int, year: Int): Flow<PagingData<Article>> =
        repository.getArticles(month, year)
}