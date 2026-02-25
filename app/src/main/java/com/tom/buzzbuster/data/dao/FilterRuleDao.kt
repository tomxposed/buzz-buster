package com.tom.buzzbuster.data.dao

import androidx.room.*
import com.tom.buzzbuster.data.model.FilterRule
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterRuleDao {

    @Query("SELECT * FROM filter_rules ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<FilterRule>>

    @Query("SELECT * FROM filter_rules WHERE isEnabled = 1 ORDER BY updatedAt DESC")
    fun getEnabled(): Flow<List<FilterRule>>

    @Query("SELECT * FROM filter_rules WHERE isEnabled = 1")
    suspend fun getEnabledList(): List<FilterRule>

    @Query("SELECT * FROM filter_rules WHERE targetPackage = :packageName OR targetPackage IS NULL")
    suspend fun getForPackage(packageName: String): List<FilterRule>

    @Query("SELECT * FROM filter_rules WHERE id = :id")
    suspend fun getById(id: Long): FilterRule?

    @Query("SELECT COUNT(*) FROM filter_rules")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM filter_rules WHERE isEnabled = 1")
    fun getActiveCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: FilterRule): Long

    @Update
    suspend fun update(rule: FilterRule)

    @Delete
    suspend fun delete(rule: FilterRule)

    @Query("DELETE FROM filter_rules WHERE id = :id")
    suspend fun deleteById(id: Long)
}
