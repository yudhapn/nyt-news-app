package id.ypran.nytnews.ui

import androidx.lifecycle.ViewModel
import id.ypran.core.article.domain.model.Article
import id.ypran.core.article.domain.usecase.LikeArticlesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val likeArticlesUseCase: LikeArticlesUseCase
) : ViewModel() {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCleared() {
        super.onCleared()
        ioScope.coroutineContext.cancel()
    }

    fun likeArticle(article: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            likeArticlesUseCase.execute(article)
        }
    }
}