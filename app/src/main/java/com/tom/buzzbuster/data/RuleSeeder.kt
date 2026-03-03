package com.tom.buzzbuster.data

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.tom.buzzbuster.data.model.FilterRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Seeds preloaded filter rules into the database on first launch.
 * Only inserts rules for apps that are currently installed on the device.
 */
object RuleSeeder {

    private const val TAG = "RuleSeeder"

    /**
     * Checks whether rules have already been seeded. If not, inserts
     * preloaded rules for every installed app and marks seeding as done.
     */
    suspend fun seedIfNeeded(context: Context) {
        val repository = BuzzBusterRepository.getInstance(context)
        val preferences = repository.preferences

        // Check if we've already seeded
        if (preferences.isRulesSeeded()) {
            Log.d(TAG, "Rules already seeded, skipping.")
            return
        }

        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            var totalInserted = 0

            for ((packageName, ruleDefs) in PreloadedRules.APP_RULES) {
                if (!isAppInstalled(pm, packageName)) {
                    Log.d(TAG, "Skipping $packageName (not installed)")
                    continue
                }

                Log.d(TAG, "Seeding ${ruleDefs.size} rules for $packageName")
                for (ruleDef in ruleDefs) {
                    repository.insertRule(
                        FilterRule(
                            name = ruleDef.name,
                            filterType = ruleDef.filterType,
                            pattern = ruleDef.pattern,
                            targetPackage = packageName,
                            isEnabled = true
                        )
                    )
                    totalInserted++
                }
            }

            Log.d(TAG, "Seeding complete — $totalInserted rules inserted.")
            preferences.setRulesSeeded(true)
        }
    }

    private fun isAppInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
