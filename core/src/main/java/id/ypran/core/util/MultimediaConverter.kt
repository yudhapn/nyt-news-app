package id.ypran.core.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.ypran.core.datasource.local.entity.MultimediaEntity

class MultimediaConverter {
    @TypeConverter
    fun multimediaListToJson(list: List<MultimediaEntity>): String = Gson().toJson(list)

    @TypeConverter
    fun jsonToMultimediaList(jsonList: String): List<MultimediaEntity> {
        val type = object : TypeToken<List<MultimediaEntity>>() {}.type
        return Gson().fromJson(jsonList, type)
    }
}