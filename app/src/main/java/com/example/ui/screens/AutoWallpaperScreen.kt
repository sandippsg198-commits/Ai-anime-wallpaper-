package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AutoWallpaperHistoryEntity
import com.example.data.model.AnimeStyle
import com.example.data.model.AutoChangeInterval
import com.example.data.model.AutoChangeSettings
import com.example.data.model.WallpaperTarget
import com.example.ui.theme.AnimeDarkBackground
import com.example.ui.theme.AnimeSurfaceCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AutoWallpaperScreen(
    settings: AutoChangeSettings,
    history: List<AutoWallpaperHistoryEntity>,
    isTriggering: Boolean,
    onUpdateSettings: (AutoChangeSettings) -> Unit,
    onTriggerNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("auto_wallpaper_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Auto-Wallpaper",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Automatically refresh your home and lock screens with fresh 4K anime artwork",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Master Switch Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.dp,
                        if (settings.isEnabled) NeonPink else BorderSubtle,
                        RoundedCornerShape(22.dp)
                    )
                    .testTag("auto_master_switch_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Auto-Changer",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (settings.isEnabled) "Active: Rotating backgrounds automatically" else "Disabled: Turn on to schedule automatic changes",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (settings.isEnabled) EmeraldMint else TextMuted
                        )
                    }

                    Switch(
                        checked = settings.isEnabled,
                        onCheckedChange = { isChecked ->
                            onUpdateSettings(settings.copy(isEnabled = isChecked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonPink,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = AnimeDarkBackground
                        ),
                        modifier = Modifier.testTag("auto_wallpaper_master_switch")
                    )
                }
            }
        }

        // Status & Next Run Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = GoldGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Engine Status",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            color = if (settings.isEnabled) EmeraldMint.copy(alpha = 0.2f) else AnimeDarkBackground,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (settings.isEnabled) EmeraldMint else BorderSubtle
                            )
                        ) {
                            Text(
                                text = if (settings.isEnabled) "ACTIVE" else "PAUSED",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (settings.isEnabled) EmeraldMint else TextMuted,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Applied", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${settings.totalChangesCount} Wallpapers",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricCyan
                            )
                        }
                        Column {
                            Text("Frequency", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = settings.interval.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }
                        Column {
                            Text("Target", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = settings.target.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = CosmicPurple
                            )
                        }
                    }

                    // Instant "Trigger Now" Button
                    Button(
                        onClick = onTriggerNow,
                        enabled = !isTriggering,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("trigger_auto_change_now_btn")
                    ) {
                        if (isTriggering) {
                            CircularProgressIndicator(
                                color = AnimeDarkBackground,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Applying Next Anime Wallpaper...", color = AnimeDarkBackground)
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = AnimeDarkBackground,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Trigger Auto-Change Now (Test)",
                                color = AnimeDarkBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Interval Frequency Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rotation Interval",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AutoChangeInterval.entries.forEach { interval ->
                            val isSelected = settings.interval == interval
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onUpdateSettings(settings.copy(interval = interval))
                                },
                                label = { Text(interval.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonPink,
                                    selectedLabelColor = Color.White,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Screen Target Selection (Home, Lock, Both)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Apply Target",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WallpaperTarget.entries.forEach { target ->
                            val isSelected = settings.target == target
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onUpdateSettings(settings.copy(target = target))
                                },
                                label = { Text(target.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CosmicPurple,
                                    selectedLabelColor = Color.White,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Style Filter Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Style Pool",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    // Options: ALL, FAVORITES, or each AnimeStyle
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = settings.selectedStyleFilter == "ALL",
                                onClick = { onUpdateSettings(settings.copy(selectedStyleFilter = "ALL")) },
                                label = { Text("All 4 Styles") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = AnimeDarkBackground,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            FilterChip(
                                selected = settings.selectedStyleFilter == "FAVORITES",
                                onClick = { onUpdateSettings(settings.copy(selectedStyleFilter = "FAVORITES")) },
                                label = { Text("Saved Favorites Only") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonPink,
                                    selectedLabelColor = Color.White,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AnimeStyle.entries.forEach { style ->
                                val isSelected = settings.selectedStyleFilter == style.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onUpdateSettings(settings.copy(selectedStyleFilter = style.id)) },
                                    label = { Text(style.displayName, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = style.accentColor,
                                        selectedLabelColor = Color.White,
                                        containerColor = AnimeDarkBackground,
                                        labelColor = TextSecondary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // History Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GoldGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Auto-Change History",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${history.size} Applied",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
        }

        // History Items
        if (history.isEmpty()) {
            item {
                Surface(
                    color = AnimeSurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "No automatic changes recorded yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Tap 'Trigger Auto-Change Now' above to test right away!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(history, key = { it.id }) { entry ->
                Surface(
                    color = AnimeSurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = entry.wallpaperTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.targetScreen,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan
                                )
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                                Text(
                                    text = dateFormat.format(Date(entry.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Applied",
                            tint = EmeraldMint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
