package id.ypran.search.domain.usecase

import id.ypran.search.domain.model.SearchHistory
import id.ypran.search.domain.repository.SearchArticlesRepository

class SearchArticlesByTitleUseCase(private val repository: SearchArticlesRepository) {
    fun execute(
        histories: List<SearchHistory>,
        query: String,
        beginDate: String,
        endDate: String,
        sort: String
    ) =
        repository.searchArticlesByTitle(
            histories,
            query,
            beginDate,
            endDate,
            sort
        )
}