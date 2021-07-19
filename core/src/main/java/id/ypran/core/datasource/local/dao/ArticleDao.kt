package id.ypran.core.datasource.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import id.ypran.core.datasource.local.entity.ArticleEntity
import id.ypran.core.datasource.local.entity.FavoriteArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article_entities")
    fun getArticles(): PagingSource<Int, ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateArticle(article: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM article_entities")
    suspend fun clearArticles()

    @Query("SELECT * FROM favorite_article_entity")
    fun getFavoriteArticlesPaging(): PagingSource<Int, FavoriteArticleEntity>

    @Query("SELECT * FROM favorite_article_entity")
    fun getFavoriteArticles(): Flow<List<FavoriteArticleEntity>>

    @Query("SELECT * FROM favorite_article_entity WHERE articleId LIKE :articleId")
    fun getFavoriteArticle(articleId: String): FavoriteArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteArticles(favorite: FavoriteArticleEntity)

    @Query("DELETE FROM favorite_article_entity WHERE articleId LIKE :articleId")
    suspend fun deleteFavoriteArticles(articleId: String)

    @Query("SELECT CASE WHEN EXISTS(SELECT 1 FROM article_entities) THEN 0 ELSE 1 END")
    fun isArticlesEmpty(): Int
}