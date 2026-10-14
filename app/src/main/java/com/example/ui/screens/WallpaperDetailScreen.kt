package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WallpaperItem
import com.example.data.model.WallpaperTarget
import com.example.ui.components.SystemPreviewMockup
import com.example.ui.components.UpscaleComparisonSlider
import com.example.ui.components.WallpaperApplyBottomSheet
import com.example.ui.theme.AnimeDarkBackground
import com.example.ui.theme.AnimeSurface
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

enum class PreviewMode {
    NONE,
    LOCK_SCREEN,
    HOME_SCREEN
}

@Composable
fun WallpaperDetailScreen(
    wallpaper: WallpaperItem,
    onBackClick: () -> Unit,
    onFavoriteToggle: (WallpaperItem) -> Unit,
    onToggleUpscale: (WallpaperItem) -> Unit,
    onApplyWallpaper: (WallpaperItem, WallpaperTarget) -> Unit,
    isApplying: Boolean,
    applySuccess: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showApplyBottomSheet by remember { mutableStateOf(false) }
    var previewMode by remember { mutableStateOf(PreviewMode.NONE) }
    var showUpscaleSplitComparison by remember { mutableStateOf(false) }

    BackHandler {
        onBackClick()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AnimeDarkBackground)
            .testTag("wallpaper_detail_screen")
    ) {
        // Main Wallpaper Visual / Comparison
        if (showUpscaleSplitComparison) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 70.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Interactive 4K Upscale Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricCyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Drag the vertical slider left and right to inspect high-fidelity anime line sharpness and HDR texture boost",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                UpscaleComparisonSlider(
                    wallpaper = wallpaper,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        } else {
            // Full Screen Display
            Box(modifier = Modifier.fillMaxSize()) {
                if (wallpaper.drawableRes != null) {
                    Image(
                        painter = painterResource(id = wallpaper.drawableRes),
                        contentDescription = wallpaper.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (wallpaper.customUri != null) {
                    AsyncImage(
                        model = wallpaper.customUri,
                        contentDescription = wallpaper.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // If upscaled, subtle radiance bloom
                if (wallpaper.isUpscaled) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        AnimeDarkBackground.copy(alpha = 0.6f),
                                        AnimeDarkBackground.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )
                }

                // System Overlay preview when toggled
                when (previewMode) {
                    PreviewMode.LOCK_SCREEN -> SystemPreviewMockup(isLockScreen = true)
                    PreviewMode.HOME_SCREEN -> SystemPreviewMockup(isLockScreen = false)
                    PreviewMode.NONE -> Unit
                }
            }
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(42.dp)
                    .background(AnimeDarkBackground.copy(alpha = 0.75f), CircleShape)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .testTag("detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            // Mode Selector: Clean, Lock, Home
            Surface(
                color = AnimeDarkBackground.copy(alpha = 0.8f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { previewMode = PreviewMode.NONE },
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (previewMode == PreviewMode.NONE) ElectricCyan else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Full Image View",
                            tint = if (previewMode == PreviewMode.NONE) AnimeDarkBackground else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { previewMode = PreviewMode.LOCK_SCREEN },
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (previewMode == PreviewMode.LOCK_SCREEN) CosmicPurple else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Screen Mockup",
                            tint = if (previewMode == PreviewMode.LOCK_SCREEN) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { previewMode = PreviewMode.HOME_SCREEN },
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (previewMode == PreviewMode.HOME_SCREEN) NeonPink else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Home Screen Mockup",
                            tint = if (previewMode == PreviewMode.HOME_SCREEN) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Favorite Button
            IconButton(
                onClick = { onFavoriteToggle(wallpaper) },
                modifier = Modifier
                    .size(42.dp)
                    .background(AnimeDarkBackground.copy(alpha = 0.75f), CircleShape)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .testTag("detail_favorite_button")
            ) {
                Icon(
                    imageVector = if (wallpaper.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (wallpaper.isFavorite) NeonPink else TextPrimary
                )
            }
        }

        // Bottom Controls & Specs Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AnimeDarkBackground.copy(alpha = 0.9f),
                            AnimeDarkBackground
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Info Row: Title, Resolution, Style
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = wallpaper.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = wallpaper.style.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = wallpaper.style.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "•", color = TextMuted, fontSize = 10.sp)
                        Text(
                            text = wallpaper.resolution,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                    }
                }

                // Interactive 4K Compare Toggle
                OutlinedButton(
                    onClick = { showUpscaleSplitComparison = !showUpscaleSplitComparison },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (showUpscaleSplitComparison) ElectricCyan else BorderSubtle
                    ),
                    modifier = Modifier.testTag("toggle_upscale_slider_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = null,
                        tint = if (showUpscaleSplitComparison) ElectricCyan else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showUpscaleSplitComparison) "Full View" else "Inspect 4K",
                        color = if (showUpscaleSplitComparison) ElectricCyan else TextPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            // Primary Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Apply Wallpaper Button
                Button(
                    onClick = { showApplyBottomSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    modifier = Modifier
                        .weight(1.8f)
                        .height(54.dp)
                        .testTag("set_wallpaper_primary_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Set Wallpaper",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Download Button
                FilledTonalButton(
                    onClick = {
                        Toast.makeText(context, "Saved 4K Wallpaper to Gallery!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("download_wallpaper_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save", color = TextPrimary)
                }
            }
        }

        // Apply Wallpaper Target Bottom Sheet
        if (showApplyBottomSheet) {
            WallpaperApplyBottomSheet(
                onDismiss = { showApplyBottomSheet = false },
                onApplyTarget = { target ->
                    onApplyWallpaper(wallpaper, target)
                    showApplyBottomSheet = false
                    Toast.makeText(context, "Applied to ${target.displayName}!", Toast.LENGTH_SHORT).show()
                },
                isApplying = isApplying,
                appliedSuccess = applySuccess
            )
        }
    }
}
