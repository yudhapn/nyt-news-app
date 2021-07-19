package id.ypran.core.util

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun dateToLong(date: Date) = date.time

    @TypeConverter
    fun longToDate(millisTime: Long): Date = Calendar.getInstance().apply {
        timeInMillis = millisTime
    }.time
}