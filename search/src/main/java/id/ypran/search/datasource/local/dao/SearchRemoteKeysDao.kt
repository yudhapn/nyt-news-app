package id.ypran.search.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ypran.search.datasource.local.entity.SearchRemoteKeysEntity

@Dao
interface SearchRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticleRemoteKeys(searchKeyEntities: List<SearchRemoteKeysEntity>)

    @Query("SELECT * FROM search_remote_keys_entities WHERE articleSearchId = :articleSearchId")
    suspend fun remoteKeysArticleId(articleSearchId: String): SearchRemoteKeysEntity?

    @Query("DELETE FROM search_remote_keys_entities")
    suspend fun clearArticleRemoteKeys()
}