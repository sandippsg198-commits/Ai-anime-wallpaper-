package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.database.AutoWallpaperHistoryEntity
import com.example.data.model.AnimeStyle
import com.example.data.model.AutoChangeSettings
import com.example.data.model.WallpaperItem
import com.example.data.model.WallpaperTarget
import com.example.data.repository.WallpaperRepository
import com.example.service.WallpaperManagerHelper
import com.example.service.WallpaperScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface AiGenerationStatus {
    object Idle : AiGenerationStatus
    object Generating : AiGenerationStatus
    data class Success(val generatedItem: WallpaperItem) : AiGenerationStatus
    data class Error(val message: String) : AiGenerationStatus
}

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WallpaperRepository(application)

    private val _selectedStyleFilter = MutableStateFlow<String>("ALL")
    val selectedStyleFilter: StateFlow<String> = _selectedStyleFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedWallpaperForDetail = MutableStateFlow<WallpaperItem?>(null)
    val selectedWallpaperForDetail: StateFlow<WallpaperItem?> = _selectedWallpaperForDetail.asStateFlow()

    private val _isApplyingWallpaper = MutableStateFlow(false)
    val isApplyingWallpaper: StateFlow<Boolean> = _isApplyingWallpaper.asStateFlow()

    private val _applySuccess = MutableStateFlow(false)
    val applySuccess: StateFlow<Boolean> = _applySuccess.asStateFlow()

    private val _aiGenerationStatus = MutableStateFlow<AiGenerationStatus>(AiGenerationStatus.Idle)
    val aiGenerationStatus: StateFlow<AiGenerationStatus> = _aiGenerationStatus.asStateFlow()

    val autoSettings: StateFlow<AutoChangeSettings> = repository.autoChangeSettings

    val autoChangeHistory: StateFlow<List<AutoWallpaperHistoryEntity>> = repository.autoChangeHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteWallpapers: StateFlow<List<WallpaperItem>> = repository.favoriteWallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredWallpapers: StateFlow<List<WallpaperItem>> = combine(
        repository.allWallpapers,
        _selectedStyleFilter,
        _searchQuery
    ) { all, styleFilter, query ->
        all.filter { item ->
            val matchesStyle = if (styleFilter == "ALL") {
                true
            } else {
                item.style.id.equals(styleFilter, ignoreCase = true)
            }

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                item.title.contains(query, ignoreCase = true) ||
                        item.tags.any { it.contains(query, ignoreCase = true) } ||
                        item.prompt.contains(query, ignoreCase = true)
            }

            matchesStyle && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setStyleFilter(styleId: String) {
        _selectedStyleFilter.value = styleId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectWallpaperForDetail(wallpaper: WallpaperItem?) {
        _selectedWallpaperForDetail.value = wallpaper
    }

    fun toggleFavorite(wallpaper: WallpaperItem) {
        viewModelScope.launch {
            repository.toggleFavorite(wallpaper.id, !wallpaper.isFavorite)
            if (_selectedWallpaperForDetail.value?.id == wallpaper.id) {
                _selectedWallpaperForDetail.value = _selectedWallpaperForDetail.value?.copy(
                    isFavorite = !wallpaper.isFavorite
                )
            }
        }
    }

    fun toggleUpscale(wallpaper: WallpaperItem) {
        viewModelScope.launch {
            repository.toggleUpscaled(wallpaper.id, !wallpaper.isUpscaled)
            if (_selectedWallpaperForDetail.value?.id == wallpaper.id) {
                _selectedWallpaperForDetail.value = _selectedWallpaperForDetail.value?.copy(
                    isUpscaled = !wallpaper.isUpscaled
                )
            }
        }
    }

    fun applyWallpaper(wallpaper: WallpaperItem, target: WallpaperTarget) {
        viewModelScope.launch {
            _isApplyingWallpaper.value = true
            _applySuccess.value = false

            delay(600) // Brief smooth rendering animation
            val success = WallpaperManagerHelper.applyWallpaper(getApplication(), wallpaper, target)

            if (success) {
                repository.logAutoChange(wallpaper, target)
                _applySuccess.value = true
            }

            _isApplyingWallpaper.value = false
            delay(1800)
            _applySuccess.value = false
        }
    }

    fun updateAutoSettings(newSettings: AutoChangeSettings) {
        repository.updateAutoChangeSettings(newSettings)
        if (newSettings.isEnabled) {
            WallpaperScheduler.schedulePeriodicAutoChange(getApplication(), newSettings.interval)
        } else {
            WallpaperScheduler.cancelAutoChange(getApplication())
        }
    }

    fun triggerAutoChangeNow() {
        viewModelScope.launch {
            _isApplyingWallpaper.value = true
            WallpaperScheduler.triggerImmediateAutoChange(getApplication())
            delay(1200)
            _isApplyingWallpaper.value = false
        }
    }

    fun generateAiAnimeWallpaper(
        style: AnimeStyle,
        subject: String,
        atmosphere: String,
        lighting: String,
        customPromptAddition: String
    ) {
        viewModelScope.launch {
            _aiGenerationStatus.value = AiGenerationStatus.Generating

            // Simulate high-fidelity AI diffusion upscale generation steps
            delay(1600)

            val fullPrompt = buildString {
                append("$subject, ")
                append("$atmosphere atmosphere, ")
                append("$lighting lighting. ")
                append(style.promptModifier)
                if (customPromptAddition.isNotBlank()) {
                    append(", $customPromptAddition")
                }
            }

            val title = "$subject (${style.displayName})"
            val drawableRes = when (style) {
                AnimeStyle.CINEMATIC_ANIME -> R.drawable.img_anime_cinematic
                AnimeStyle.WATERCOLOR -> R.drawable.img_anime_watercolor
                AnimeStyle.PIXEL_ART -> R.drawable.img_anime_pixel
                AnimeStyle.ABSTRACT -> R.drawable.img_anime_abstract
            }

            val newItem = WallpaperItem(
                id = "ai_gen_" + UUID.randomUUID().toString().take(8),
                title = title,
                style = style,
                prompt = fullPrompt,
                drawableRes = drawableRes,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = true,
                isUpscaled = true,
                upscaleModel = "AnimeDiffusion-Upscaler 4x Pro",
                tags = listOf(style.displayName, subject.take(12), "AI Studio", "4K"),
                primaryColorHex = "#121424",
                accentColorHex = String.format("#%06X", (0xFFFFFF and style.accentColor.hashCode())),
                dateAdded = System.currentTimeMillis(),
                description = "AI generated high-fidelity anime wallpaper with $atmosphere atmosphere and $lighting lighting."
            )

            repository.addCustomWallpaper(newItem)
            _aiGenerationStatus.value = AiGenerationStatus.Success(newItem)
            _selectedWallpaperForDetail.value = newItem
        }
    }

    fun resetAiGenerationStatus() {
        _aiGenerationStatus.value = AiGenerationStatus.Idle
    }
}
