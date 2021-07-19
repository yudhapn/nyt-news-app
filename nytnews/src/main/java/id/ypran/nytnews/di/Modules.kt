package id.ypran.nytnews.di

import id.ypran.nytnews.ui.ArticleDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(searchComponent)
}

val viewModelModule: Module = module {
    viewModel { ArticleDetailViewModel(get()) }
}

val searchComponent = listOf(viewModelModule)