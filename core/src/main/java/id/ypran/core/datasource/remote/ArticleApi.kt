package id.ypran.core.datasource.remote

import id.ypran.core.datasource.remote.response.ArticleListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleApi {
    @GET("archive/v1/{year}/{month}.json")
    suspend fun getArticles(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("api-key") apiKey: String
    ): ArticleListResponse
}