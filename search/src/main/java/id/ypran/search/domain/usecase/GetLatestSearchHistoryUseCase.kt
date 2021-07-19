package id.ypran.search.domain.usecase

import id.ypran.search.domain.repository.SearchArticlesRepository

class GetLatestSearchHistoryUseCase(private val repository: SearchArticlesRepository) {
    suspend fun execute() = repository.getLatestSearchHistory()
}
