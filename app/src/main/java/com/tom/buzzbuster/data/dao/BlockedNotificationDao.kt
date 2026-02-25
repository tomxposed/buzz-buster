package com.tom.buzzbuster.data.dao

import androidx.room.*
import com.tom.buzzbuster.data.model.BlockedNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedNotificationDao {

    @Query("SELECT * FROM blocked_notifications ORDER BY blockedAt DESC")
    fun getAll(): Flow<List<BlockedNotification>>

    @Query(
        """
        SELECT * FROM blocked_notifications 
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%' 
        OR appName LIKE '%' || :query || '%'
        OR packageName LIKE '%' || :query || '%'
        ORDER BY blockedAt DESC
        """
    )
    fun search(query: String): Flow<List<BlockedNotification>>

    @Query("SELECT * FROM blocked_notifications WHERE blockedAt BETWEEN :start AND :end ORDER BY blockedAt DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<BlockedNotification>>

    @Query("SELECT COUNT(*) FROM blocked_notifications")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM blocked_notifications WHERE blockedAt >= :since")
    fun getCountSince(since: Long): Flow<Int>

    @Query("SELECT * FROM blocked_notifications WHERE id = :id")
    suspend fun getById(id: Long): BlockedNotification?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: BlockedNotification): Long

    @Query("UPDATE blocked_notifications SET isRestored = 1 WHERE id = :id")
    suspend fun markRestored(id: Long)

    @Delete
    suspend fun delete(notification: BlockedNotification)

    @Query("DELETE FROM blocked_notifications WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM blocked_notifications WHERE blockedAt < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM blocked_notifications")
    suspend fun deleteAll()

    @Query(
        """
        DELETE FROM blocked_notifications WHERE id NOT IN (
            SELECT id FROM blocked_notifications ORDER BY blockedAt DESC LIMIT :limit
        )
        """
    )
    suspend fun trimToLimit(limit: Int)
}
