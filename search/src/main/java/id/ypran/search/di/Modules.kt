package id.ypran.search.di

import android.app.Application
import androidx.room.Room
import id.ypran.core.createNetworkClient
import id.ypran.kozziinews.di.BASE_URL
import id.ypran.kozziinews.di.NYT_NEWS_API_KEY
import id.ypran.search.data.repository.SearchArticlesRepositoryImpl
import id.ypran.search.datasource.local.SearchArticlesDb
import id.ypran.search.datasource.local.dao.SearchArticleDao
import id.ypran.search.datasource.mediator.SearchArticlesRemoteMediator
import id.ypran.search.datasource.remote.ArticleSearchApi
import id.ypran.search.domain.repository.SearchArticlesRepository
import id.ypran.search.domain.usecase.GetLatestSearchHistoryUseCase
import id.ypran.search.domain.usecase.SearchArticlesByTitleUseCase
import id.ypran.search.ui.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(searchComponent)
}

val viewModelModule: Module = module {
    viewModel { SearchViewModel(get(), get()) }
}

val useCaseModule: Module = module {
    factory { SearchArticlesByTitleUseCase(get()) }
    factory { GetLatestSearchHistoryUseCase(get()) }
}

val repositoryModule: Module = module {
    single { SearchArticlesRepositoryImpl(get()) as SearchArticlesRepository }
}

val pagingSourceModule: Module = module {
    factory { (query: String, beginDate: String, endDate: String, sort: String) ->
        SearchArticlesRemoteMediator(
            articleSearchApi = get(),
            searchArticlesDb = get(),
            query = query,
            beginDate = beginDate,
            endDate = endDate,
            sort = sort,
            key = NYT_NEWS_API_KEY
        )
    }
}

val databaseModule: Module = module {
    fun provideDatabase(application: Application): SearchArticlesDb = Room.databaseBuilder(
        application,
        SearchArticlesDb::class.java,
        "articles-search-db"
    )
        .fallbackToDestructiveMigration()
        .build()

    fun provideSearchRemoteKeysDao(database: SearchArticlesDb) = database.searchRemoteKeysDao

    single { provideDatabase(androidApplication()) }
    single { provideSearchRemoteKeysDao(get()) }
}

val localSourceModule: Module = module {
    fun provideSearchCatalogueDao(database: SearchArticlesDb): SearchArticleDao =
        database.searchArticleDao

    single { provideSearchCatalogueDao(get()) }
}

val remoteSourceModule: Module = module {
    single { articlesSearchApi }
}

private val retrofit: Retrofit = createNetworkClient(BASE_URL)
private val articlesSearchApi: ArticleSearchApi = retrofit.create(ArticleSearchApi::class.java)

val searchComponent = listOf(
    viewModelModule,
    useCaseModule,
    repositoryModule,
    pagingSourceModule,
    databaseModule,
    localSourceModule,
    remoteSourceModule
)