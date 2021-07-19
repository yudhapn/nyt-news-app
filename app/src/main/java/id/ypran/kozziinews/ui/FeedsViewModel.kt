package id.ypran.kozziinews.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.usecase.GetArticlesUseCase
import id.ypran.core.article.domain.usecase.LikeArticlesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedsViewModel(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val likeArticlesUseCase: LikeArticlesUseCase
) : ViewModel() {
    var articles: Flow<PagingData<Article>>? = null

    init {
        getArticles()
    }

    private fun getArticles() {
        articles = getArticlesUseCase.execute(month = 12, year = 2019).cachedIn(viewModelScope)
    }

    fun likeArticle(article: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            likeArticlesUseCase.execute(article)
        }
    }
}