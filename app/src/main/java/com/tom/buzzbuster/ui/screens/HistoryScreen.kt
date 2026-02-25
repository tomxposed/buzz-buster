package com.tom.buzzbuster.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tom.buzzbuster.data.model.BlockedNotification
import com.tom.buzzbuster.ui.components.*
import com.tom.buzzbuster.ui.theme.*
import com.tom.buzzbuster.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedNotification by remember { mutableStateOf<BlockedNotification?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Header ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (state.notifications.isNotEmpty()) {
                IconButton(onClick = { showDeleteAllDialog = true }) {
                    Icon(
                        Icons.Rounded.DeleteSweep,
                        contentDescription = "Clear all",
                        tint = ErrorRed
                    )
                }
            }
        }

        // ── Search Bar ──────────────────────────────────
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = {
                Text(
                    "Search by app, title, or content...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Crimson,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Notification List ───────────────────────────
        if (state.notifications.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No blocked notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Intercepted notifications will appear here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            val grouped = viewModel.getGroupedByDate()
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                grouped.forEach { (date, notifications) ->
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(notifications, key = { it.id }) { notification ->
                        BlockedNotificationCard(
                            notification = notification,
                            onClick = { selectedNotification = notification },
                            onRestore = { viewModel.restoreNotification(notification) },
                            onDelete = { viewModel.deleteNotification(notification) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // ── Detail Sheet ────────────────────────────────────
    selectedNotification?.let { notification ->
        NotificationDetailSheet(
            notification = notification,
            onDismiss = { selectedNotification = null },
            onRestore = {
                viewModel.restoreNotification(notification)
                selectedNotification = null
            },
            onDelete = {
                viewModel.deleteNotification(notification)
                selectedNotification = null
            }
        )
    }

    // ── Delete All Dialog ───────────────────────────────
    if (showDeleteAllDialog) {
        ConfirmDialog(
            title = "Clear All History",
            message = "This will permanently delete all blocked notification records. This cannot be undone.",
            confirmLabel = "Clear All",
            isDestructive = true,
            onConfirm = { viewModel.deleteAll() },
            onDismiss = { showDeleteAllDialog = false }
        )
    }
}

@Composable
private fun BlockedNotificationCard(
    notification: BlockedNotification,
    onClick: () -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = if (notification.isRestored) 0.5f else 1f
            )
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App icon placeholder
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Crimson.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = notification.appName.take(1).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Crimson,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.appName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Crimson,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatTime(notification.blockedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (notification.isRestored) {
                    StatusBadge(label = "Restored", color = SuccessGreen)
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                        IconButton(
                            onClick = onRestore,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Restore,
                                contentDescription = "Restore",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Delete",
                                tint = ErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (notification.content.isNotBlank()) {
                Text(
                    text = notification.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (notification.matchedRuleName != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.Rule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Matched: ${notification.matchedRuleName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    notification.matchType?.let {
                        StatusBadge(
                            label = it.replace("_", " "),
                            color = when (it) {
                                "STRING_MATCH" -> InfoBlue
                                "REGEX" -> WarningAmber
                                "AI_GENERATED" -> Crimson
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationDetailSheet(
    notification: BlockedNotification,
    onDismiss: () -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Notification Detail",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            DetailRow("App", notification.appName)
            DetailRow("Package", notification.packageName)
            DetailRow("Title", notification.title)
            DetailRow("Content", notification.content)
            DetailRow("Blocked At", formatDateTime(notification.blockedAt))
            notification.matchedRuleName?.let { DetailRow("Matched Rule", it) }
            notification.matchType?.let { DetailRow("Match Type", it.replace("_", " ")) }
            DetailRow("Status", if (notification.isRestored) "Restored" else "Blocked")

            Spacer(modifier = Modifier.height(8.dp))

            if (!notification.isRestored) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete")
                    }
                    AccentButton(
                        text = "Restore",
                        onClick = onRestore,
                        icon = Icons.Rounded.Restore,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun formatDateTime(timestamp: Long): String {
    return SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(timestamp))
}
