package id.ypran.search.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_remote_keys_entities")
data class SearchRemoteKeysEntity(
    @PrimaryKey
    val articleSearchId: String,
    val prevKey: Int?,
    val nextKey: Int?
)