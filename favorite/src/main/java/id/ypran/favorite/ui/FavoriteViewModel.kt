package id.ypran.favorite.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.usecase.LikeArticlesUseCase
import id.ypran.favorite.domain.usecase.GetFavoriteArticlesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val getFavoriteArticlesUseCase: GetFavoriteArticlesUseCase,
    private val likeArticlesUseCase: LikeArticlesUseCase
) : ViewModel() {
    var favoriteArticles: Flow<PagingData<Article>>? = null

    init {
        getFavoriteArticles()
    }

    private fun getFavoriteArticles() {
        favoriteArticles = getFavoriteArticlesUseCase.execute().cachedIn(viewModelScope)
    }

    fun likeArticle(article: Article) {
        CoroutineScope(Dispatchers.IO).launch { likeArticlesUseCase.execute(article) }
    }
}