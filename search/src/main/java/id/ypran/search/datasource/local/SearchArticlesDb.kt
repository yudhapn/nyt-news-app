package id.ypran.search.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.ypran.core.datasource.local.entity.ArticleEntity
import id.ypran.core.util.DateConverter
import id.ypran.core.util.MultimediaConverter
import id.ypran.search.datasource.local.dao.SearchArticleDao
import id.ypran.search.datasource.local.dao.SearchHistoryDao
import id.ypran.search.datasource.local.dao.SearchRemoteKeysDao
import id.ypran.search.datasource.local.entity.SearchHistoryEntity
import id.ypran.search.datasource.local.entity.SearchRemoteKeysEntity

@Database(
    entities = [ArticleEntity::class, SearchHistoryEntity::class, SearchRemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MultimediaConverter::class, DateConverter::class)
abstract class SearchArticlesDb : RoomDatabase() {
    abstract val searchArticleDao: SearchArticleDao
    abstract val searchHistoryDao: SearchHistoryDao
    abstract val searchRemoteKeysDao: SearchRemoteKeysDao
}