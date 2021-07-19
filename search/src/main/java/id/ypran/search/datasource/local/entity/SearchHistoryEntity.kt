package id.ypran.search.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import id.ypran.search.domain.model.SearchHistory
import java.util.*

@Entity(tableName = "search_history_entity")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @field:SerializedName("id")
    val id: Int = 0,
    @field:SerializedName("keyword")
    val keyword: String,
    @field:SerializedName("executeAt")
    val executeAt: Date = Calendar.getInstance().time
)

fun List<SearchHistoryEntity>.mapToDomain(): List<SearchHistory> = map { it.mapToDomain() }

fun SearchHistoryEntity.mapToDomain(): SearchHistory = SearchHistory(
    id = id,
    keyword = keyword,
    executeAt = executeAt
)

fun List<SearchHistory>.mapToEntity(): List<SearchHistoryEntity> = map { it.mapToEntity() }

fun SearchHistory.mapToEntity(): SearchHistoryEntity = SearchHistoryEntity(
    id = id,
    keyword = keyword,
    executeAt = executeAt
)