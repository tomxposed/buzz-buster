package com.tom.buzzbuster.data

import android.content.Context
import com.tom.buzzbuster.data.dao.BlockedNotificationDao
import com.tom.buzzbuster.data.dao.FilterRuleDao
import com.tom.buzzbuster.data.model.BlockedNotification
import com.tom.buzzbuster.data.model.FilterRule
import kotlinx.coroutines.flow.Flow

class BuzzBusterRepository(context: Context) {

    private val database = AppDatabase.getInstance(context)
    val filterRuleDao: FilterRuleDao = database.filterRuleDao()
    val blockedNotificationDao: BlockedNotificationDao = database.blockedNotificationDao()
    val preferences = PreferencesManager(context)

    // ── Filter Rules ────────────────────────────────────────
    fun getAllRules(): Flow<List<FilterRule>> = filterRuleDao.getAll()
    fun getEnabledRules(): Flow<List<FilterRule>> = filterRuleDao.getEnabled()
    suspend fun getEnabledRulesList(): List<FilterRule> = filterRuleDao.getEnabledList()
    suspend fun getRulesForPackage(pkg: String): List<FilterRule> = filterRuleDao.getForPackage(pkg)
    suspend fun getRuleById(id: Long): FilterRule? = filterRuleDao.getById(id)
    fun getRuleCount(): Flow<Int> = filterRuleDao.getTotalCount()
    fun getActiveRuleCount(): Flow<Int> = filterRuleDao.getActiveCount()
    suspend fun insertRule(rule: FilterRule): Long = filterRuleDao.insert(rule)
    suspend fun updateRule(rule: FilterRule) = filterRuleDao.update(rule)
    suspend fun deleteRule(rule: FilterRule) = filterRuleDao.delete(rule)
    suspend fun deleteRuleById(id: Long) = filterRuleDao.deleteById(id)

    // ── Blocked Notifications ───────────────────────────────
    fun getAllBlocked(): Flow<List<BlockedNotification>> = blockedNotificationDao.getAll()
    fun searchBlocked(query: String): Flow<List<BlockedNotification>> = blockedNotificationDao.search(query)
    fun getBlockedCount(): Flow<Int> = blockedNotificationDao.getTotalCount()
    fun getBlockedCountSince(since: Long): Flow<Int> = blockedNotificationDao.getCountSince(since)
    suspend fun insertBlocked(notification: BlockedNotification): Long = blockedNotificationDao.insert(notification)
    suspend fun markRestored(id: Long) = blockedNotificationDao.markRestored(id)
    suspend fun deleteBlocked(notification: BlockedNotification) = blockedNotificationDao.delete(notification)
    suspend fun deleteBlockedById(id: Long) = blockedNotificationDao.deleteById(id)
    suspend fun deleteOlderThan(before: Long) = blockedNotificationDao.deleteOlderThan(before)
    suspend fun deleteAllBlocked() = blockedNotificationDao.deleteAll()
    suspend fun trimHistory(limit: Int) = blockedNotificationDao.trimToLimit(limit)

    companion object {
        @Volatile
        private var INSTANCE: BuzzBusterRepository? = null

        fun getInstance(context: Context): BuzzBusterRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = BuzzBusterRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
