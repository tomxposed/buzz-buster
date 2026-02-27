package com.tom.buzzbuster.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tom.buzzbuster.R
import com.tom.buzzbuster.ui.theme.*

@Composable
fun AboutScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── Back button row ─────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── App Logo ────────────────────────────────────
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = CircleShape,
                    ambientColor = Crimson.copy(alpha = 0.3f),
                    spotColor = Crimson.copy(alpha = 0.4f)
                )
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "BuzzBuster Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── App Name ────────────────────────────────────
        Text(
            text = "Buzz Buster",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Tagline ─────────────────────────────────────
        Text(
            text = "Silence the spam. Keep the signal.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Version Badge ───────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Crimson.copy(alpha = 0.1f)
        ) {
            Text(
                text = "v1.0.0 • Stable",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Crimson
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Info Cards ──────────────────────────────────
        AboutInfoCard(
            icon = Icons.Rounded.Person,
            title = "Developer",
            value = "Tom Varghese",
            iconTint = Crimson
        )

        Spacer(modifier = Modifier.height(12.dp))

        AboutInfoCard(
            icon = Icons.Rounded.Gavel,
            title = "License",
            value = "MIT License",
            iconTint = WarningAmber
        )

        Spacer(modifier = Modifier.height(12.dp))

        AboutInfoCard(
            icon = Icons.Rounded.Code,
            title = "Source Code",
            value = "github.com/tomxposed/buzz-buster",
            iconTint = InfoBlue,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tomxposed/buzz-buster"))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AboutInfoCard(
            icon = Icons.Rounded.Build,
            title = "Built With",
            value = "Kotlin • Jetpack Compose • Material 3",
            iconTint = SuccessGreen
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Divider ─────────────────────────────────────
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 32.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Footer ──────────────────────────────────────
        Text(
            text = "Made with ❤\uFE0F in India",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "© 2026 Tom. All rights reserved.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun AboutInfoCard(
    icon: ImageVector,
    title: String,
    value: String,
    iconTint: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (onClick != null) {
                Icon(
                    Icons.Rounded.OpenInNew,
                    contentDescription = "Open link",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
