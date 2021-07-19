package id.ypran.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import id.ypran.search.domain.model.SearchHistory
import id.ypran.search.domain.usecase.GetLatestSearchHistoryUseCase
import id.ypran.search.domain.usecase.SearchArticlesByTitleUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchArticlesByTitleUseCase: SearchArticlesByTitleUseCase,
    private val getLatestSearchHistoryUseCase: GetLatestSearchHistoryUseCase
) : ViewModel() {
    private val _searchHistories = MutableLiveData<List<SearchHistory>>()
    val searchHistories: LiveData<List<SearchHistory>>
        get() = _searchHistories

    init {
        getLatestSearchHistory()
    }

    fun searchCatalogue(query: String) = searchArticlesByTitleUseCase.execute(
        histories = _searchHistories.value ?: emptyList(),
        query = query,
        beginDate = "20120101",
        endDate = "20201231",
        sort = "newest"
    ).cachedIn(viewModelScope)

    private fun getLatestSearchHistory() = viewModelScope.launch {
        getLatestSearchHistoryUseCase.execute().collect {
            _searchHistories.postValue(it)
        }
    }
}