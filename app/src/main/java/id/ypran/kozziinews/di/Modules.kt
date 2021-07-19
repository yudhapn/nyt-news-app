package id.ypran.kozziinews.di

import android.app.Application
import androidx.room.Room
import id.ypran.core.BuildConfig
import id.ypran.core.article.data.repository.ArticleRepositoryImpl
import id.ypran.core.article.domain.repository.ArticleRepository
import id.ypran.core.article.domain.usecase.GetArticlesUseCase
import id.ypran.core.article.domain.usecase.GetFavoriteArticlesUseCase
import id.ypran.core.article.domain.usecase.LikeArticlesUseCase
import id.ypran.core.createNetworkClient
import id.ypran.core.datasource.local.ArticleDb
import id.ypran.core.datasource.mediator.ArticleRemoteMediator
import id.ypran.core.datasource.remote.ArticleApi
import id.ypran.kozziinews.ui.FeedsViewModel
import id.ypran.kozziinews.ui.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { FeedsViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { GetArticlesUseCase(get()) }
    factory { LikeArticlesUseCase(get()) }
    factory { GetFavoriteArticlesUseCase(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): ArticleDb =
        Room.databaseBuilder(application, ArticleDb::class.java, "article-db")
            .fallbackToDestructiveMigration()
            .build()
    single { provideDatabase(androidApplication()) }
}

val localDataSourceModule = module {
    fun provideArticleDao(database: ArticleDb) = database.articleDao
    fun provideArticleRemoteKeysDao(database: ArticleDb) = database.articleRemoteKeysDao

    single { provideArticleDao(get()) }
    single { provideArticleRemoteKeysDao(get()) }
}

val remoteDataSourceModule = module {
    single { articleApi }
}

val repositoryModule = module {
    single {
        ArticleRepositoryImpl(articleDb = get()) as ArticleRepository
    }
}

val pagingSourceModule = module {
    single { (month: Int, year: Int) ->
        ArticleRemoteMediator(
            articleApi = articleApi,
            articleDb = get(),
            key = NYT_NEWS_API_KEY,
            month = month,
            year = year
        )
    }
}

const val BASE_URL = "https://api.nytimes.com/svc/"
const val NYT_NEWS_API_KEY = BuildConfig.NYT_NEWS_API_KEY

private val retrofit = createNetworkClient(BASE_URL)
private val articleApi: ArticleApi = retrofit.create(ArticleApi::class.java)

val appComponent = listOf(
    databaseModule,
    localDataSourceModule,
    remoteDataSourceModule,
    pagingSourceModule,
    repositoryModule,
    viewModelModule,
    useCaseModule
)