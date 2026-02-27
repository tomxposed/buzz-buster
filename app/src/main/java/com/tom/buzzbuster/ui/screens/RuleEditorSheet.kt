package com.tom.buzzbuster.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tom.buzzbuster.data.model.FilterRule
import com.tom.buzzbuster.data.model.FilterType
import com.tom.buzzbuster.ui.components.*
import com.tom.buzzbuster.ui.theme.*
import com.tom.buzzbuster.ui.viewmodel.RulesViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RuleEditorSheet(
    rule: FilterRule?,
    viewModel: RulesViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditing = rule != null

    var name by remember { mutableStateOf(rule?.name ?: "") }
    var filterType by remember { mutableStateOf(rule?.filterType ?: FilterType.STRING_MATCH) }
    var pattern by remember { mutableStateOf(rule?.pattern ?: "") }
    var targetPackage by remember { mutableStateOf(rule?.targetPackage ?: "") }
    var aiPrompt by remember { mutableStateOf(rule?.originalPrompt ?: "") }
    var showAppPicker by remember { mutableStateOf(false) }

    val selectedPackages = remember {
        mutableStateListOf<String>().apply {
            if (!rule?.targetPackage.isNullOrBlank()) {
                addAll(rule!!.targetPackage!!.split(",").map { it.trim() })
            }
        }
    }

    // Keep targetPackage string in sync
    LaunchedEffect(selectedPackages.toList()) {
        targetPackage = selectedPackages.joinToString(",")
    }

    val context = LocalContext.current
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        installedApps = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val pm = context.packageManager
            pm.getInstalledApplications(0)
                .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                .map { AppInfo(it.packageName, pm.getApplicationLabel(it).toString()) }
                .sortedBy { it.name.lowercase() }
        }
    }

    val selectedAppsLabel = remember(selectedPackages.toList(), installedApps) {
        when {
            selectedPackages.isEmpty() -> null
            selectedPackages.size == 1 -> installedApps.find { it.packageName == selectedPackages[0] }?.name ?: selectedPackages[0]
            else -> "${selectedPackages.size} apps selected"
        }
    }

    // Update pattern when AI generates result
    LaunchedEffect(state.aiResult) {
        if (state.aiResult != null) {
            pattern = state.aiResult!!
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Title ───────────────────────────────────
            Text(
                text = if (isEditing) "Edit Rule" else "New Rule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // ── Name Field ──────────────────────────────
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Rule Name") },
                placeholder = { Text("e.g., Block Promo SMS") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Crimson,
                    focusedLabelColor = Crimson
                )
            )

            // ── Filter Type ─────────────────────────────
            Text(
                text = "FILTER TYPE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTypeChip(
                    label = "String",
                    selected = filterType == FilterType.STRING_MATCH,
                    onClick = { filterType = FilterType.STRING_MATCH }
                )
                FilterTypeChip(
                    label = "Regex",
                    selected = filterType == FilterType.REGEX,
                    onClick = { filterType = FilterType.REGEX }
                )
                if (state.hasApiKey) {
                    FilterTypeChip(
                        label = "AI Generate",
                        selected = filterType == FilterType.AI_GENERATED,
                        onClick = { filterType = FilterType.AI_GENERATED }
                    )
                }
            }

            // ── AI Prompt (only for AI_GENERATED) ───────
            AnimatedVisibility(visible = filterType == FilterType.AI_GENERATED) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = { aiPrompt = it },
                        label = { Text("Describe Your Intent") },
                        placeholder = { Text("e.g., silence marketing offers but prioritize delivery tracking") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Crimson,
                            focusedLabelColor = Crimson
                        )
                    )

                    AccentButton(
                        text = if (state.aiGenerating) "Generating..." else "Generate Regex",
                        onClick = { viewModel.generateRegexFromIntent(aiPrompt) },
                        enabled = aiPrompt.isNotBlank() && !state.aiGenerating,
                        icon = Icons.Rounded.AutoAwesome,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.aiGenerating) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp)),
                            color = Crimson
                        )
                    }

                    if (state.aiError != null) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.Error,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Something went wrong. Please try again.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ErrorRed
                                )
                            }
                        }
                    }
                }
            }

            // ── Pattern Field ───────────────────────────
            OutlinedTextField(
                value = pattern,
                onValueChange = { pattern = it },
                label = {
                    Text(
                        when (filterType) {
                            FilterType.STRING_MATCH -> "Match String"
                            FilterType.REGEX -> "Regex Pattern"
                            FilterType.AI_GENERATED -> "Generated Regex"
                        }
                    )
                },
                placeholder = {
                    Text(
                        when (filterType) {
                            FilterType.STRING_MATCH -> "e.g., limited offer, flash sale, act now"
                            FilterType.REGEX -> "e.g., (?:sale|offer|discount).*\\d+%"
                            FilterType.AI_GENERATED -> "AI-generated pattern will appear here"
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Crimson,
                    focusedLabelColor = Crimson
                )
            )

            // ── Target App ──────────────────────────────
            Text(
                text = "TARGET APP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = { showAppPicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Rounded.Apps,
                            contentDescription = null,
                            tint = if (selectedPackages.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else Crimson,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = selectedAppsLabel ?: "All Apps (global)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedPackages.isEmpty()) FontWeight.Normal else FontWeight.SemiBold,
                            color = if (selectedPackages.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (selectedPackages.isNotEmpty()) {
                        IconButton(
                            onClick = { selectedPackages.clear() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Leave as All Apps to apply globally",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedPackages.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        selectedPackages.toList().forEach { pkg ->
                            val appName = installedApps.find { it.packageName == pkg }?.name ?: pkg
                            InputChip(
                                selected = true,
                                onClick = { selectedPackages.remove(pkg) },
                                label = {
                                    Text(
                                        appName,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Rounded.Close,
                                        contentDescription = "Remove $appName",
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = Crimson.copy(alpha = 0.12f),
                                    selectedLabelColor = Crimson,
                                    selectedTrailingIconColor = Crimson
                                ),
                                border = InputChipDefaults.inputChipBorder(
                                    enabled = true,
                                    selected = true,
                                    selectedBorderColor = Crimson.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }

            // ── Save Button ─────────────────────────────
            AccentButton(
                text = if (isEditing) "Update Rule" else "Create Rule",
                onClick = {
                    if (isEditing && rule != null) {
                        viewModel.updateRule(
                            rule.copy(
                                name = name,
                                filterType = filterType,
                                pattern = pattern,
                                targetPackage = targetPackage.takeIf { it.isNotBlank() },
                                originalPrompt = if (filterType == FilterType.AI_GENERATED) aiPrompt else null
                            )
                        )
                    } else {
                        viewModel.addRule(
                            name = name,
                            filterType = filterType,
                            pattern = pattern,
                            targetPackage = targetPackage.takeIf { it.isNotBlank() },
                            originalPrompt = if (filterType == FilterType.AI_GENERATED) aiPrompt else null
                        )
                    }
                    onDismiss()
                },
                enabled = name.isNotBlank() && pattern.isNotBlank(),
                icon = if (isEditing) Icons.Rounded.Save else Icons.Rounded.Add,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // ── App Picker Dialog ───────────────────────────────
    if (showAppPicker) {
        AppPickerDialog(
            apps = installedApps,
            selectedPackages = selectedPackages.toSet(),
            onConfirm = { selected ->
                selectedPackages.clear()
                selectedPackages.addAll(selected)
                showAppPicker = false
            },
            onDismiss = { showAppPicker = false }
        )
    }
}

private data class AppInfo(
    val packageName: String,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPickerDialog(
    apps: List<AppInfo>,
    selectedPackages: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val localSelection = remember { mutableStateListOf<String>().apply { addAll(selectedPackages) } }

    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.isBlank()) apps
        else apps.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (localSelection.isEmpty()) "Select Apps"
                else "Selected (${localSelection.size})",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search apps...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Crimson,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(filteredApps) { app ->
                        val isSelected = app.packageName in localSelection
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    Crimson.copy(alpha = 0.08f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            onClick = {
                                if (isSelected) localSelection.remove(app.packageName)
                                else localSelection.add(app.packageName)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        if (isSelected) localSelection.remove(app.packageName)
                                        else localSelection.add(app.packageName)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Crimson,
                                        checkmarkColor = androidx.compose.ui.graphics.Color.White
                                    ),
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = app.packageName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(localSelection.toSet()) }) {
                Text("Done", color = Crimson, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
