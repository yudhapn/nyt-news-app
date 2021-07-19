package id.ypran.search.datasource.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ypran.core.datasource.local.entity.ArticleEntity

@Dao
interface SearchArticleDao {
    @Query("SELECT * FROM article_entities WHERE headline LIKE :query OR leadParagraph LIKE :query OR snippet LIKE :query")
    fun searchArticleByTitle(query: String): PagingSource<Int, ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSearchResult(searchResult: List<ArticleEntity>)

    @Query("DELETE FROM article_entities")
    suspend fun clearSearch()
}