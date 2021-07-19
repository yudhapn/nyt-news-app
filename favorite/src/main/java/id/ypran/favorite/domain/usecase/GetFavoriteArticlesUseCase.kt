package id.ypran.favorite.domain.usecase

import id.ypran.core.article.domain.repository.ArticleRepository

class GetFavoriteArticlesUseCase(private val repository: ArticleRepository) {
    fun execute() = repository.getFavoriteArticles()
}
