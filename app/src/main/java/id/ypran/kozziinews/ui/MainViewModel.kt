package id.ypran.kozziinews.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.ypran.core.article.domain.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainViewModel : ViewModel() {
    var articles: Flow<PagingData<Article>>? = null
    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int>
        get() = _position

    init {
        setPosition(0)
    }

    fun setArticles(data: PagingData<Article>) {
        articles = flow {
            emit(data)
        }.cachedIn(viewModelScope)
    }

    fun setPosition(position: Int) {
        _position.value = position
    }
}