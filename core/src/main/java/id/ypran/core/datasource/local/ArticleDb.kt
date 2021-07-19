package id.ypran.core.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.ypran.core.datasource.local.dao.ArticleDao
import id.ypran.core.datasource.local.dao.ArticleRemoteKeysDao
import id.ypran.core.datasource.local.entity.ArticleEntity
import id.ypran.core.datasource.local.entity.ArticleRemoteKeysEntity
import id.ypran.core.datasource.local.entity.FavoriteArticleEntity
import id.ypran.core.util.MultimediaConverter

@Database(
    entities = [ArticleEntity::class, FavoriteArticleEntity::class, ArticleRemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MultimediaConverter::class)
abstract class ArticleDb : RoomDatabase() {
    abstract val articleDao: ArticleDao
    abstract val articleRemoteKeysDao: ArticleRemoteKeysDao
}