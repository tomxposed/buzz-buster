package com.tom.buzzbuster.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blocked_notifications",
    foreignKeys = [
        ForeignKey(
            entity = FilterRule::class,
            parentColumns = ["id"],
            childColumns = ["matchedRuleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("matchedRuleId"), Index("blockedAt")]
)
data class BlockedNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val title: String,
    val content: String,
    val matchedRuleId: Long? = null,
    val matchedRuleName: String? = null,
    val matchType: String? = null, // STRING_MATCH, REGEX, AI_GENERATED
    val blockedAt: Long = System.currentTimeMillis(),
    val isRestored: Boolean = false
)
