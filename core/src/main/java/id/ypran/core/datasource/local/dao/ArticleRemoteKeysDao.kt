package id.ypran.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ypran.core.datasource.local.entity.ArticleRemoteKeysEntity

@Dao
interface ArticleRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticleRemoteKeys(articleKeyEntities: List<ArticleRemoteKeysEntity>)

    @Query("SELECT * FROM article_remote_keys_entities WHERE articleId = :articleId")
    suspend fun remoteKeysArticleId(articleId: String): ArticleRemoteKeysEntity?

    @Query("DELETE FROM article_remote_keys_entities")
    suspend fun clearArticleRemoteKeys()
}