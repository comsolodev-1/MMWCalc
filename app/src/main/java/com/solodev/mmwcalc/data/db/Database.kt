package com.solodev.mmwcalc.data.db

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solodev.mmwcalc.domain.models.StepItem
import kotlinx.coroutines.flow.Flow

// ─── Type Converters ──────────────────────────────────────────────────────────

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStepList(steps: List<StepItem>): String =
        gson.toJson(steps)

    @TypeConverter
    fun toStepList(json: String): List<StepItem> {
        val type = object : TypeToken<List<StepItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringMap(map: Map<String, String>): String =
        gson.toJson(map)

    @TypeConverter
    fun toStringMap(json: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
}

// ─── Entity ───────────────────────────────────────────────────────────────────

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val topicId: String,
    val topicName: String,
    val solvedFor: String,
    val solvedForLabel: String,
    val answer: String,
    val steps: List<StepItem>,
    val inputs: Map<String, String>,
    val timestampMs: Long = System.currentTimeMillis()
)

// ─── DAO ──────────────────────────────────────────────────────────────────────

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY timestampMs DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getById(id: Int): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntity): Long

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM history")
    suspend fun clearAll()
}

// ─── Database ─────────────────────────────────────────────────────────────────

@Database(
    entities = [HistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        const val DATABASE_NAME = "mmwcalc_history.db"
    }
}