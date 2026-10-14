package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnimeStyle
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.ArtStylesScreen
import com.example.ui.screens.AutoWallpaperScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.WallpaperDetailScreen
import com.example.ui.theme.AnimeDarkBackground
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.WallpaperViewModel

enum class AppNavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DISCOVER("Discover", Icons.Filled.Explore, Icons.Outlined.Explore),
    STYLES("Styles", Icons.Filled.Palette, Icons.Outlined.Palette),
    AI_STUDIO("AI Studio", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    AUTO("Auto", Icons.Filled.Autorenew, Icons.Outlined.Autorenew),
    SAVED("Saved", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
}

class MainActivity : ComponentActivity() {
    private val viewModel: WallpaperViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: WallpaperViewModel) {
    var currentTab by remember { mutableStateOf(AppNavDestination.DISCOVER) }
    var studioPreselectedStyle by remember { mutableStateOf(AnimeStyle.CINEMATIC_ANIME) }

    val wallpapers by viewModel.filteredWallpapers.collectAsStateWithLifecycle()
    val favoriteWallpapers by viewModel.favoriteWallpapers.collectAsStateWithLifecycle()
    val selectedStyleFilter by viewModel.selectedStyleFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedWallpaper by viewModel.selectedWallpaperForDetail.collectAsStateWithLifecycle()
    val isApplying by viewModel.isApplyingWallpaper.collectAsStateWithLifecycle()
    val applySuccess by viewModel.applySuccess.collectAsStateWithLifecycle()
    val autoSettings by viewModel.autoSettings.collectAsStateWithLifecycle()
    val autoHistory by viewModel.autoChangeHistory.collectAsStateWithLifecycle()
    val aiGenStatus by viewModel.aiGenerationStatus.collectAsStateWithLifecycle()

    // Handle Back button when looking at WallpaperDetailScreen
    BackHandler(enabled = selectedWallpaper != null) {
        viewModel.selectWallpaperForDetail(null)
    }

    Box(modifier = Modifier.fillMaxSize().background(AnimeDarkBackground)) {
        if (selectedWallpaper != null) {
            // Wallpaper Detail Screen overlay
            WallpaperDetailScreen(
                wallpaper = selectedWallpaper!!,
                onBackClick = { viewModel.selectWallpaperForDetail(null) },
                onFavoriteToggle = { viewModel.toggleFavorite(it) },
                onToggleUpscale = { viewModel.toggleUpscale(it) },
                onApplyWallpaper = { item, target -> viewModel.applyWallpaper(item, target) },
                isApplying = isApplying,
                applySuccess = applySuccess
            )
        } else {
            // Main Bottom Navigation Scaffold
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = AnimeDarkBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    NavigationBar(
                        containerColor = AnimeSurfaceCard,
                        contentColor = TextPrimary,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("main_bottom_nav_bar")
                    ) {
                        AppNavDestination.entries.forEach { destination ->
                            val isSelected = currentTab == destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = destination },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = destination.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = destination.title,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimeDarkBackground,
                                    selectedTextColor = NeonPink,
                                    indicatorColor = NeonPink,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            AppNavDestination.DISCOVER -> {
                                DiscoverScreen(
                                    wallpapers = wallpapers,
                                    selectedStyleFilter = selectedStyleFilter,
                                    searchQuery = searchQuery,
                                    onStyleFilterSelected = { viewModel.setStyleFilter(it) },
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onWallpaperClick = { viewModel.selectWallpaperForDetail(it) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(it) }
                                )
                            }
                            AppNavDestination.STYLES -> {
                                ArtStylesScreen(
                                    onExploreStyle = { style ->
                                        viewModel.setStyleFilter(style.id)
                                        currentTab = AppNavDestination.DISCOVER
                                    },
                                    onGenerateWithStyle = { style ->
                                        studioPreselectedStyle = style
                                        currentTab = AppNavDestination.AI_STUDIO
                                    }
                                )
                            }
                            AppNavDestination.AI_STUDIO -> {
                                AiStudioScreen(
                                    currentStyle = studioPreselectedStyle,
                                    onStyleSelected = { studioPreselectedStyle = it },
                                    generationStatus = aiGenStatus,
                                    onGenerate = { style, subject, atmosphere, lighting, extra ->
                                        viewModel.generateAiAnimeWallpaper(
                                            style = style,
                                            subject = subject,
                                            atmosphere = atmosphere,
                                            lighting = lighting,
                                            customPromptAddition = extra
                                        )
                                    }
                                )
                            }
                            AppNavDestination.AUTO -> {
                                AutoWallpaperScreen(
                                    settings = autoSettings,
                                    history = autoHistory,
                                    isTriggering = isApplying,
                                    onUpdateSettings = { viewModel.updateAutoSettings(it) },
                                    onTriggerNow = { viewModel.triggerAutoChangeNow() }
                                )
                            }
                            AppNavDestination.SAVED -> {
                                FavoritesScreen(
                                    favoriteWallpapers = favoriteWallpapers,
                                    onWallpaperClick = { viewModel.selectWallpaperForDetail(it) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                                    onExploreClick = { currentTab = AppNavDestination.DISCOVER }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
