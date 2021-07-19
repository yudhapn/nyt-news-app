package id.ypran.search.datasource.remote

import id.ypran.core.datasource.remote.response.ArticleListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleSearchApi {
    @GET("search/v2/articlesearch.json")
    suspend fun getSearchArticlesResult(
        @Query("api-key") apiKey: String,
        @Query("q") query: String,
        @Query("begin_date") beginDate: String,
        @Query("end_date") endDate: String,
        @Query("page") page: Int,
        @Query("sort") sort: String
    ): ArticleListResponse

}