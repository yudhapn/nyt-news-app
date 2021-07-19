package id.ypran.core.article.domain.usecase

import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.repository.ArticleRepository

class LikeArticlesUseCase(private val repository: ArticleRepository) {
    suspend fun execute(article: Article) = repository.likeArticle(article)
}
