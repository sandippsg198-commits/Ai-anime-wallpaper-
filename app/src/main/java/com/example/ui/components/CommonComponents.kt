package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AnimeStyle
import com.example.data.model.WallpaperItem
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

@Composable
fun AnimeStyleBadge(
    style: AnimeStyle,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = if (isSelected) style.accentColor.copy(alpha = 0.25f) else AnimeSurfaceCard
    val borderColor = if (isSelected) style.accentColor else BorderSubtle

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("style_badge_${style.id}"),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(style.accentColor)
            )
            Text(
                text = style.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) style.accentColor else TextPrimary
            )
        }
    }
}

@Composable
fun WallpaperCard(
    wallpaper: WallpaperItem,
    onWallpaperClick: (WallpaperItem) -> Unit,
    onFavoriteToggle: (WallpaperItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
            .clickable { onWallpaperClick(wallpaper) }
            .testTag("wallpaper_card_${wallpaper.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
        ) {
            // Background Artwork
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

            // Subtle gradient overlay for contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                AnimeDarkBackground.copy(alpha = 0.85f),
                                AnimeDarkBackground.copy(alpha = 0.98f)
                            )
                        )
                    )
            )

            // Top Badges Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Style Badge
                Surface(
                    color = AnimeDarkBackground.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, wallpaper.style.accentColor)
                ) {
                    Text(
                        text = wallpaper.style.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = wallpaper.style.accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Favorite button
                IconButton(
                    onClick = { onFavoriteToggle(wallpaper) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(AnimeDarkBackground.copy(alpha = 0.65f), CircleShape)
                        .testTag("favorite_button_${wallpaper.id}")
                ) {
                    Icon(
                        imageVector = if (wallpaper.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (wallpaper.isFavorite) NeonPink else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom Info Column
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = ElectricCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, ElectricCyan)
                    ) {
                        Text(
                            text = "4K HDR",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    if (wallpaper.isUpscaled) {
                        Surface(
                            color = CosmicPurple.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, CosmicPurple)
                        ) {
                            Text(
                                text = "UPSCALED",
                                style = MaterialTheme.typography.labelSmall,
                                color = CosmicPurple,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = wallpaper.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = wallpaper.tags.take(2).joinToString(" • ") { "#$it" },
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Interactive Before & After comparison slider for Upscaled visuals.
 * Allows user to slide left/right to compare Standard 1080p vs AI Upscaled 4K HDR.
 */
@Composable
fun UpscaleComparisonSlider(
    wallpaper: WallpaperItem,
    modifier: Modifier = Modifier
) {
    var splitPositionFraction by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .testTag("upscale_comparison_slider")
    ) {
        val totalWidth = maxWidth
        val splitOffset = totalWidth * splitPositionFraction

        // Layer 1: Left Side - Standard Original (with slight simulated soft resolution)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (wallpaper.drawableRes != null) {
                Image(
                    painter = painterResource(id = wallpaper.drawableRes),
                    contentDescription = "Standard Original",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Layer 2: Right Side - AI Upscaled 4K HDR Crisp Visuals
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    val clipRect = Rect(
                        left = size.width * splitPositionFraction,
                        top = 0f,
                        right = size.width,
                        bottom = size.height
                    )
                    val path = Path().apply { addRect(clipRect) }
                    clipPath(path) {
                        this@drawWithContent.drawContent()
                    }
                }
        ) {
            if (wallpaper.drawableRes != null) {
                Image(
                    painter = painterResource(id = wallpaper.drawableRes),
                    contentDescription = "AI Upscaled 4K",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Micro-sharpening & color grading boost overlay for upscaled region
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ElectricCyan.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Draggable vertical divider line
        Box(
            modifier = Modifier
                .offset(x = splitOffset - 2.dp)
                .width(4.dp)
                .fillMaxHeight()
                .background(Color.White)
        )

        // Draggable slider handle in the center
        Box(
            modifier = Modifier
                .offset(x = splitOffset - 18.dp)
                .align(Alignment.CenterStart)
                .size(36.dp)
                .clip(CircleShape)
                .background(AnimeDarkBackground)
                .border(2.dp, ElectricCyan, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newFraction = (splitPositionFraction + (dragAmount.x / size.width))
                            .coerceIn(0.05f, 0.95f)
                        splitPositionFraction = newFraction
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Slide to compare",
                tint = ElectricCyan,
                modifier = Modifier.size(20.dp)
            )
        }

        // Top labels: "STANDARD 1080P" (Left) and "AI 4K UPSCALED" (Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = AnimeDarkBackground.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(
                    text = "ORIGINAL 1080P",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = AnimeDarkBackground.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "AI 4K UPSCALED",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Simulates Android System UI (Lock Screen Clock or Home Screen App Icons)
 * so users can test how the wallpaper looks in real daily use!
 */
@Composable
fun SystemPreviewMockup(
    isLockScreen: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
    ) {
        // System status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "09:41",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "5G • 100%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }

        if (isLockScreen) {
            // Lock screen big clock & date
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-60).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "09:41",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = "Wednesday, October 14",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Lock screen bottom actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            // Home screen app grid mockup
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 60.dp)
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // App row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    repeat(4) { idx ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        when (idx) {
                                            0 -> NeonPink.copy(alpha = 0.8f)
                                            1 -> ElectricCyan.copy(alpha = 0.8f)
                                            2 -> CosmicPurple.copy(alpha = 0.8f)
                                            else -> GoldGlow.copy(alpha = 0.8f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Widgets,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = listOf("Apps", "Music", "Gallery", "Anime")[idx],
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }

                // Dock row
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bottom Sheet for applying wallpaper to Home screen, Lock screen, or Both.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperApplyBottomSheet(
    onDismiss: () -> Unit,
    onApplyTarget: (WallpaperTarget) -> Unit,
    isApplying: Boolean,
    appliedSuccess: Boolean
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AnimeSurfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Apply Anime Wallpaper",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "Select display target with real Android WallpaperManager",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            if (isApplying) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = NeonPink, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Rendering 4K ultra-high fidelity...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            } else if (appliedSuccess) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = EmeraldMint,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Wallpaper Applied Successfully!",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldMint
                    )
                }
            } else {
                // Target Options
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        onClick = { onApplyTarget(WallpaperTarget.HOME_SCREEN) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("apply_home_screen_btn")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = ElectricCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Set on Home Screen", color = TextPrimary)
                    }

                    FilledTonalButton(
                        onClick = { onApplyTarget(WallpaperTarget.LOCK_SCREEN) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("apply_lock_screen_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = CosmicPurple)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Set on Lock Screen", color = TextPrimary)
                    }

                    FilledTonalButton(
                        onClick = { onApplyTarget(WallpaperTarget.BOTH) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("apply_both_screens_btn")
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = NeonPink)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Set on Both (Home & Lock)", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
