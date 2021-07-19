package id.ypran.favorite.datasource.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ypran.core.datasource.local.entity.ArticleEntity

@Dao
interface FavoriteArticleDao {
    @Query("SELECT * FROM article_entities")
    fun getFavoriteArticles(): PagingSource<Int, ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavorites(searchResult: List<ArticleEntity>)

    @Query("DELETE FROM article_entities")
    suspend fun clearFavorite()
}