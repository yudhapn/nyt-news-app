package id.ypran.favorite.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.ypran.core.datasource.local.entity.ArticleEntity
import id.ypran.core.util.DateConverter
import id.ypran.core.util.MultimediaConverter
import id.ypran.favorite.datasource.local.dao.FavoriteArticleDao

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MultimediaConverter::class, DateConverter::class)
abstract class FavoriteArticlesDb : RoomDatabase() {
    abstract val favoriteArticleDao: FavoriteArticleDao
}