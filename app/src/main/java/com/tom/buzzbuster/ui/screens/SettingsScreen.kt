package com.tom.buzzbuster.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tom.buzzbuster.ui.components.*
import com.tom.buzzbuster.ui.theme.*
import com.tom.buzzbuster.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showHistoryLimitMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showNukeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── Header ──────────────────────────────────────
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        // ── Interface ───────────────────────────────────
        SettingsSection(title = "Interface") {
            // Theme mode
            SettingsRow(
                icon = Icons.Rounded.DarkMode,
                title = "Theme",
                subtitle = when (state.themeMode) {
                    "dark" -> "Dark Mode"
                    "light" -> "Light Mode"
                    else -> "System Default"
                },
                iconTint = Crimson
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ThemeChip("dark", "Dark", state.themeMode) { viewModel.setThemeMode("dark") }
                    ThemeChip("light", "Light", state.themeMode) { viewModel.setThemeMode("light") }
                    ThemeChip("system", "Auto", state.themeMode) { viewModel.setThemeMode("system") }
                }
            }
        }

        // ── Workflow ────────────────────────────────────
        SettingsSection(title = "Workflow") {
            SettingsRow(
                icon = Icons.Rounded.Key,
                title = "Gemini API Key",
                subtitle = if (state.geminiApiKey.isNotBlank()) "Configured ✓" else "Not configured",
                iconTint = WarningAmber,
                onClick = { showApiKeyDialog = true }
            ) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Privacy ─────────────────────────────────────
        SettingsSection(title = "Privacy") {
            Box {
                SettingsRow(
                    icon = Icons.Rounded.Storage,
                    title = "History Limit",
                    subtitle = "Records to keep",
                    iconTint = InfoBlue,
                    onClick = { showHistoryLimitMenu = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${state.historyLimit}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                DropdownMenu(
                    expanded = showHistoryLimitMenu,
                    onDismissRequest = { showHistoryLimitMenu = false }
                ) {
                    listOf(100, 250, 500, 1000, 2500).forEach { limit ->
                        DropdownMenuItem(
                            text = { Text("$limit records") },
                            onClick = {
                                viewModel.setHistoryLimit(limit)
                                showHistoryLimitMenu = false
                            },
                            leadingIcon = {
                                if (state.historyLimit == limit) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Crimson
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // ── About ────────────────────────────────────────
        SettingsSection(title = "About") {
            SettingsRow(
                icon = Icons.Rounded.Info,
                title = "About BuzzBuster",
                subtitle = "Version 1.0",
                iconTint = Crimson,
                onClick = { /* TODO: About dialog */ }
            ) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            SettingsRow(
                icon = Icons.Rounded.PrivacyTip,
                title = "Privacy Policy",
                subtitle = "Data handling spec",
                iconTint = InfoBlue,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/privacy"))
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            SettingsRow(
                icon = Icons.Rounded.BugReport,
                title = "Report Issue",
                subtitle = "Bug tracker",
                iconTint = WarningAmber,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@buzzbuster.app")
                        putExtra(Intent.EXTRA_SUBJECT, "BuzzBuster Bug Report")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Danger Zone ─────────────────────────────────
        SettingsSection(title = "Danger Zone") {
            SettingsRow(
                icon = Icons.Rounded.RestartAlt,
                title = "Restore Defaults",
                subtitle = "Reset preferences",
                iconTint = WarningAmber,
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            SettingsRow(
                icon = Icons.Rounded.DeleteForever,
                title = "Delete All Data",
                subtitle = "Irreversible wipedown",
                iconTint = ErrorRed,
                onClick = { showNukeDialog = true }
            ) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Footer ──────────────────────────────────────
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "BUZZ BUSTER V1.0 • STABLE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(100.dp))
    }

    // ── API Key Dialog ──────────────────────────────────
    if (showApiKeyDialog) {
        var apiKey by remember { mutableStateOf(state.geminiApiKey) }
        var showKey by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = {
                Text("Gemini API Key", fontWeight = FontWeight.SemiBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Enter your Google Gemini API key to enable AI-powered regex generation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        singleLine = true,
                        visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showKey = !showKey }) {
                                Icon(
                                    if (showKey) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Crimson,
                            focusedLabelColor = Crimson
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setGeminiApiKey(apiKey)
                    showApiKeyDialog = false
                }) {
                    Text("Save", color = Crimson, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ── Reset Dialog ────────────────────────────────────
    if (showResetDialog) {
        ConfirmDialog(
            title = "Restore Defaults",
            message = "This will reset all settings to their default values. Your rules and history will be preserved.",
            confirmLabel = "Reset",
            isDestructive = true,
            onConfirm = { viewModel.resetDefaults() },
            onDismiss = { showResetDialog = false }
        )
    }

    // ── Nuke Dialog ─────────────────────────────────────
    if (showNukeDialog) {
        ConfirmDialog(
            title = "Delete All Data",
            message = "⚠️ This will permanently delete ALL blocked notification history and reset all settings. This action CANNOT be undone.",
            confirmLabel = "Delete Everything",
            isDestructive = true,
            onConfirm = { viewModel.nukeAllData() },
            onDismiss = { showNukeDialog = false }
        )
    }
}

@Composable
private fun ThemeChip(
    mode: String,
    label: String,
    currentMode: String,
    onClick: () -> Unit
) {
    FilterTypeChip(
        label = label,
        selected = currentMode == mode,
        onClick = onClick
    )
}
