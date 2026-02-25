package com.tom.buzzbuster.ui.screens

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tom.buzzbuster.ui.components.*
import com.tom.buzzbuster.ui.theme.*
import com.tom.buzzbuster.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToRules: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var isListenerEnabled by remember { mutableStateOf(isNotificationListenerEnabled(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                isListenerEnabled = isNotificationListenerEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── App Header ──────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Buzz Buster",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            InterceptorToggle(
                checked = state.isInterceptorEnabled,
                onCheckedChange = { viewModel.toggleInterceptor() }
            )
        }

        if (!isListenerEnabled) {
            AccentButton(
                text = "Grant Notification Access",
                onClick = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                },
                icon = Icons.Rounded.Security,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Stats Grid ──────────────────────────────────
        SectionHeader(title = "Statistics")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                title = "BLOCKED TODAY",
                value = state.blockedToday.toString(),
                icon = Icons.Rounded.Shield,
                accentColor = Crimson,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = "TOTAL BLOCKED",
                value = state.totalBlocked.toString(),
                icon = Icons.Rounded.Block,
                accentColor = CrimsonLight,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                title = "TOTAL RULES",
                value = state.totalRules.toString(),
                icon = Icons.Rounded.FilterAlt,
                accentColor = InfoBlue,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = "ACTIVE RULES",
                value = state.activeRules.toString(),
                icon = Icons.Rounded.ToggleOn,
                accentColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Quick Actions ───────────────────────────────
        SectionHeader(title = "Quick Actions")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = onNavigateToRules
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Crimson,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            "New Rule",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Create filter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = onNavigateToHistory
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Rounded.History,
                        contentDescription = null,
                        tint = CrimsonLight,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            "View Logs",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Blocked history",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // padding for nav bar
    }
}

private fun isNotificationListenerEnabled(context: android.content.Context): Boolean {
    val cn = ComponentName(context, "com.tom.buzzbuster.service.NotificationInterceptorService")
    val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return flat != null && flat.contains(cn.flattenToString())
}

@Composable
private fun InterceptorToggle(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    val thumbPosition by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "thumb"
    )

    val trackColor = if (checked) Crimson else MaterialTheme.colorScheme.outline
    val trackWidth = 90.dp
    val trackHeight = 36.dp
    val thumbSize = 28.dp
    val thumbPadding = 4.dp

    Box(
        modifier = Modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCheckedChange
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // Label text
        Text(
            text = if (checked) "Active" else "Inactive",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(
                    start = if (checked) 14.dp else 0.dp,
                    end = if (checked) 0.dp else 12.dp
                )
                .then(
                    if (checked) Modifier.align(Alignment.CenterStart)
                    else Modifier.align(Alignment.CenterEnd)
                )
        )

        // Thumb
        val maxOffset = trackWidth - thumbSize - thumbPadding * 2
        Box(
            modifier = Modifier
                .padding(start = thumbPadding)
                .offset { IntOffset(x = (maxOffset.toPx() * thumbPosition).toInt(), y = 0) }
                .size(thumbSize)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
