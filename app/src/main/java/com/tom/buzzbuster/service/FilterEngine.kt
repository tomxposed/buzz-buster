package com.tom.buzzbuster.service

import com.tom.buzzbuster.data.model.FilterRule
import com.tom.buzzbuster.data.model.FilterType

data class FilterResult(
    val matched: Boolean,
    val rule: FilterRule? = null,
    val matchType: String? = null
)

object FilterEngine {

    fun evaluate(
        rules: List<FilterRule>,
        packageName: String,
        title: String,
        content: String
    ): FilterResult {
        val text = "$title $content"

        // Filter rules applicable to this package (or global rules)
        val applicable = rules.filter { rule ->
            rule.isEnabled && (rule.targetPackage == null || rule.targetPackage == packageName)
        }

        // Tier 1: String Match
        applicable.filter { it.filterType == FilterType.STRING_MATCH }.forEach { rule ->
            if (text.contains(rule.pattern, ignoreCase = true)) {
                return FilterResult(matched = true, rule = rule, matchType = "STRING_MATCH")
            }
        }

        // Tier 2: Regex
        applicable.filter {
            it.filterType == FilterType.REGEX || it.filterType == FilterType.AI_GENERATED
        }.forEach { rule ->
            try {
                val regex = Regex(rule.pattern, RegexOption.IGNORE_CASE)
                if (regex.containsMatchIn(text)) {
                    return FilterResult(
                        matched = true,
                        rule = rule,
                        matchType = if (rule.filterType == FilterType.AI_GENERATED) "AI_GENERATED" else "REGEX"
                    )
                }
            } catch (_: Exception) {
                // Invalid regex — skip this rule
            }
        }

        return FilterResult(matched = false)
    }
}
