package id.ypran.search.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ypran.search.datasource.local.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history_entity ORDER BY executeAt LIMIT 10")
    suspend fun getLatestHistory(): List<SearchHistoryEntity>

    @Query("SELECT * FROM search_history_entity WHERE keyword LIKE :query")
    suspend fun isHistoryExists(query: String): SearchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(histories: List<SearchHistoryEntity>)
}