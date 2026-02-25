package com.tom.buzzbuster.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FilterType {
    STRING_MATCH,
    REGEX,
    AI_GENERATED
}

@Entity(tableName = "filter_rules")
data class FilterRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val filterType: FilterType,
    val pattern: String,
    val targetPackage: String? = null, // null = global (all apps)
    val isEnabled: Boolean = true,
    val originalPrompt: String? = null, // for AI_GENERATED: the user's NL intent
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
