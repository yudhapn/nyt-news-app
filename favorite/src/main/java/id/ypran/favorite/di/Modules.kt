package id.ypran.favorite.di

import android.app.Application
import androidx.room.Room
import id.ypran.favorite.data.repository.FavoriteArticlesRepositoryImpl
import id.ypran.favorite.datasource.local.FavoriteArticlesDb
import id.ypran.favorite.datasource.local.dao.FavoriteArticleDao
import id.ypran.favorite.domain.repository.FavoriteArticlesRepository
import id.ypran.favorite.domain.usecase.GetFavoriteArticlesUseCase
import id.ypran.favorite.ui.FavoriteViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(searchComponent)
}

val viewModelModule: Module = module {
    viewModel { FavoriteViewModel(get(), get()) }
}

val useCaseModule: Module = module {
    factory { GetFavoriteArticlesUseCase(get()) }
}

val repositoryModule: Module = module {
    single { FavoriteArticlesRepositoryImpl(get()) as FavoriteArticlesRepository }
}

val databaseModule: Module = module {
    fun provideDatabase(application: Application): FavoriteArticlesDb = Room.databaseBuilder(
        application,
        FavoriteArticlesDb::class.java,
        "articles-favorite-db"
    )
        .fallbackToDestructiveMigration()
        .build()

    single { provideDatabase(androidApplication()) }
}

val localSourceModule: Module = module {
    fun provideFavoriteArticlesDao(database: FavoriteArticlesDb): FavoriteArticleDao =
        database.favoriteArticleDao

    single { provideFavoriteArticlesDao(get()) }
}

val searchComponent = listOf(
    viewModelModule,
    useCaseModule,
    repositoryModule,
    databaseModule,
    localSourceModule,
)