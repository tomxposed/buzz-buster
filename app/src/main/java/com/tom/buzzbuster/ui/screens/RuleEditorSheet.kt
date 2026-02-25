package com.tom.buzzbuster.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tom.buzzbuster.data.model.FilterRule
import com.tom.buzzbuster.data.model.FilterType
import com.tom.buzzbuster.ui.components.*
import com.tom.buzzbuster.ui.theme.*
import com.tom.buzzbuster.ui.viewmodel.RulesViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
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
                FilterTypeChip(
                    label = "AI Generate",
                    selected = filterType == FilterType.AI_GENERATED,
                    onClick = { filterType = FilterType.AI_GENERATED }
                )
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
                                    state.aiError!!,
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
                            FilterType.STRING_MATCH -> "e.g., limited offer"
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
            OutlinedTextField(
                value = targetPackage,
                onValueChange = { targetPackage = it },
                label = { Text("Target App (optional)") },
                placeholder = { Text("e.g., com.whatsapp — leave empty for all apps") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Crimson,
                    focusedLabelColor = Crimson
                )
            )
            Text(
                text = "Leave empty to apply globally to all apps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

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
}
