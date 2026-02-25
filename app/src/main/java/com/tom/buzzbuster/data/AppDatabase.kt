package com.tom.buzzbuster.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tom.buzzbuster.data.dao.BlockedNotificationDao
import com.tom.buzzbuster.data.dao.FilterRuleDao
import com.tom.buzzbuster.data.model.BlockedNotification
import com.tom.buzzbuster.data.model.FilterRule

@Database(
    entities = [FilterRule::class, BlockedNotification::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun filterRuleDao(): FilterRuleDao
    abstract fun blockedNotificationDao(): BlockedNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "buzzbuster.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
