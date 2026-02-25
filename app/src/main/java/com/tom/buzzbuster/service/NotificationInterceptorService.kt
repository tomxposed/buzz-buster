package com.tom.buzzbuster.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.tom.buzzbuster.data.BuzzBusterRepository
import com.tom.buzzbuster.data.model.BlockedNotification
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class NotificationInterceptorService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var repository: BuzzBusterRepository

    override fun onCreate() {
        super.onCreate()
        repository = BuzzBusterRepository.getInstance(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        // Don't intercept our own notifications
        if (sbn.packageName == packageName) return
        // Don't intercept ongoing/foreground service notifications
        if (sbn.isOngoing) return

        serviceScope.launch {
            try {
                // Check if interceptor is enabled
                val enabled = repository.preferences.isInterceptorEnabled.first()
                if (!enabled) return@launch

                val notification = sbn.notification ?: return@launch
                val extras = notification.extras

                val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
                val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
                val appName = getAppName(sbn.packageName)

                // Get applicable rules
                val rules = repository.getEnabledRulesList()
                val result = FilterEngine.evaluate(rules, sbn.packageName, title, text)

                if (result.matched && result.rule != null) {
                    // Log to database
                    repository.insertBlocked(
                        BlockedNotification(
                            packageName = sbn.packageName,
                            appName = appName,
                            title = title,
                            content = text,
                            matchedRuleId = result.rule.id,
                            matchedRuleName = result.rule.name,
                            matchType = result.matchType
                        )
                    )

                    // Cancel the notification
                    cancelNotification(sbn.key)

                    // Trim history if needed
                    val limit = repository.preferences.historyLimit.first()
                    repository.trimHistory(limit)
                }
            } catch (_: Exception) {
                // Silently fail — don't crash the listener service
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No action needed
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun getAppName(packageName: String): String {
        return try {
            val pm = applicationContext.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            packageName
        }
    }

    companion object {
        private const val RESTORE_CHANNEL_ID = "buzzbuster_restored"
        private const val RESTORE_CHANNEL_NAME = "Restored Notifications"

        fun restoreNotification(context: Context, blocked: BlockedNotification) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create channel if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    RESTORE_CHANNEL_ID,
                    RESTORE_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, RESTORE_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(blocked.title)
                .setContentText(blocked.content)
                .setSubText("Restored from ${blocked.appName}")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify(blocked.id.toInt(), notification)
        }
    }
}
