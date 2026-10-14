package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.data.database.AppDatabase
import com.example.data.database.AutoWallpaperHistoryEntity
import com.example.data.database.WallpaperDao
import com.example.data.database.WallpaperEntity
import com.example.data.model.AnimeStyle
import com.example.data.model.AutoChangeInterval
import com.example.data.model.AutoChangeSettings
import com.example.data.model.WallpaperItem
import com.example.data.model.WallpaperTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class WallpaperRepository(
    context: Context,
    private val wallpaperDao: WallpaperDao = AppDatabase.getDatabase(context).wallpaperDao()
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("animai_settings_prefs", Context.MODE_PRIVATE)

    private val _autoChangeSettings = MutableStateFlow(loadAutoChangeSettings())
    val autoChangeSettings: StateFlow<AutoChangeSettings> = _autoChangeSettings.asStateFlow()

    val allWallpapers: Flow<List<WallpaperItem>> = wallpaperDao.getAllWallpapers().map { entities ->
        entities.map { it.toWallpaperItem() }
    }

    val favoriteWallpapers: Flow<List<WallpaperItem>> = wallpaperDao.getFavoriteWallpapers().map { entities ->
        entities.map { it.toWallpaperItem() }
    }

    val autoChangeHistory: Flow<List<AutoWallpaperHistoryEntity>> = wallpaperDao.getAutoChangeHistory()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (wallpaperDao.getCount() == 0) {
                seedInitialWallpapers()
            }
        }
    }

    fun getWallpapersByStyle(style: AnimeStyle): Flow<List<WallpaperItem>> {
        return wallpaperDao.getWallpapersByStyle(style.id).map { list ->
            list.map { it.toWallpaperItem() }
        }
    }

    suspend fun getWallpaperById(id: String): WallpaperItem? {
        return wallpaperDao.getWallpaperById(id)?.toWallpaperItem()
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        wallpaperDao.updateFavorite(id, isFavorite)
    }

    suspend fun toggleUpscaled(id: String, isUpscaled: Boolean) {
        wallpaperDao.updateUpscaled(id, isUpscaled)
    }

    suspend fun addCustomWallpaper(item: WallpaperItem) {
        wallpaperDao.insertWallpaper(WallpaperEntity.fromWallpaperItem(item))
    }

    suspend fun logAutoChange(wallpaper: WallpaperItem, target: WallpaperTarget) {
        wallpaperDao.insertHistoryEntry(
            AutoWallpaperHistoryEntity(
                wallpaperId = wallpaper.id,
                wallpaperTitle = wallpaper.title,
                styleId = wallpaper.style.id,
                targetScreen = target.displayName,
                timestamp = System.currentTimeMillis()
            )
        )
        val currentCount = prefs.getInt("total_changes_count", 0) + 1
        val now = System.currentTimeMillis()
        val nextTime = now + (_autoChangeSettings.value.interval.hours * 3600 * 1000L)

        prefs.edit()
            .putInt("total_changes_count", currentCount)
            .putLong("last_changed_timestamp", now)
            .putLong("next_scheduled_timestamp", nextTime)
            .apply()

        _autoChangeSettings.value = _autoChangeSettings.value.copy(
            totalChangesCount = currentCount,
            lastChangedTimestamp = now,
            nextScheduledTimestamp = nextTime
        )
    }

    fun updateAutoChangeSettings(settings: AutoChangeSettings) {
        prefs.edit()
            .putBoolean("auto_enabled", settings.isEnabled)
            .putString("auto_interval", settings.interval.name)
            .putString("auto_target", settings.target.name)
            .putString("auto_style_filter", settings.selectedStyleFilter)
            .putLong("next_scheduled_timestamp", settings.nextScheduledTimestamp)
            .apply()
        _autoChangeSettings.value = settings
    }

    private fun loadAutoChangeSettings(): AutoChangeSettings {
        val enabled = prefs.getBoolean("auto_enabled", false)
        val intervalStr = prefs.getString("auto_interval", AutoChangeInterval.DAILY.name)
        val targetStr = prefs.getString("auto_target", WallpaperTarget.BOTH.name)
        val filter = prefs.getString("auto_style_filter", "ALL") ?: "ALL"
        val lastChanged = prefs.getLong("last_changed_timestamp", 0L)
        val nextScheduled = prefs.getLong("next_scheduled_timestamp", 0L)
        val totalCount = prefs.getInt("total_changes_count", 0)

        val interval = try {
            AutoChangeInterval.valueOf(intervalStr ?: AutoChangeInterval.DAILY.name)
        } catch (_: Exception) {
            AutoChangeInterval.DAILY
        }

        val target = try {
            WallpaperTarget.valueOf(targetStr ?: WallpaperTarget.BOTH.name)
        } catch (_: Exception) {
            WallpaperTarget.BOTH
        }

        return AutoChangeSettings(
            isEnabled = enabled,
            interval = interval,
            target = target,
            selectedStyleFilter = filter,
            lastChangedTimestamp = lastChanged,
            nextScheduledTimestamp = nextScheduled,
            totalChangesCount = totalCount
        )
    }

    private suspend fun seedInitialWallpapers() {
        val list = listOf(
            // 1. Cinematic Anime
            WallpaperItem(
                id = "anime_cinematic_1",
                title = "Celestial Stargazer",
                style = AnimeStyle.CINEMATIC_ANIME,
                prompt = "High-fidelity anime girl on a floating sky island gazing at celestial galaxies and glowing nebula skies, Makoto Shinkai style, volumetric clouds, 8K wallpaper",
                drawableRes = R.drawable.img_anime_cinematic,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = true,
                isUpscaled = true,
                upscaleModel = "AnimeESRGAN-4x v3.2 Ultra HDR",
                tags = listOf("Celestial", "Makoto Shinkai", "Galaxies", "Twilight", "4K"),
                primaryColorHex = "#161938",
                accentColorHex = "#FF2A85",
                description = "Masterpiece cinematic composition with dynamic atmospheric lighting and sub-pixel edge clarity."
            ),
            // 2. Watercolor Anime
            WallpaperItem(
                id = "anime_watercolor_1",
                title = "Sakura Koi Sanctuary",
                style = AnimeStyle.WATERCOLOR,
                prompt = "Delicate anime watercolor illustration, peaceful shrine maiden near tranquil cherry blossom koi pond, fluid pastel pigment bleed, cold-press paper grain",
                drawableRes = R.drawable.img_anime_watercolor,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = true,
                isUpscaled = true,
                upscaleModel = "AnimeTexture-Artisan 4x HDR",
                tags = listOf("Watercolor", "Cherry Blossom", "Koi Pond", "Traditional", "Zen"),
                primaryColorHex = "#1A2E3B",
                accentColorHex = "#38BDF8",
                description = "Hand-painted organic watercolor washes with authentic pigment diffusion and subtle paper texture."
            ),
            // 3. 16-Bit Pixel Art
            WallpaperItem(
                id = "anime_pixel_1",
                title = "Neo-Tokyo Rainy Alley",
                style = AnimeStyle.PIXEL_ART,
                prompt = "Retro 16-bit cyber anime pixel art, neon-lit alleyway in Neo-Tokyo with anime cyborg girl wearing illuminated jacket, rain puddles reflecting holographic signs, nostalgic lo-fi",
                drawableRes = R.drawable.img_anime_pixel,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = false,
                isUpscaled = true,
                upscaleModel = "PixelClean-SuperRes 4x",
                tags = listOf("Pixel Art", "Cyberpunk", "Neo-Tokyo", "Lo-Fi", "16-Bit"),
                primaryColorHex = "#26152B",
                accentColorHex = "#F59E0B",
                description = "Integer-scaled 16-bit cyber nostalgia with meticulous dithered reflections and CRT glow simulation."
            ),
            // 4. Abstract Anime
            WallpaperItem(
                id = "anime_abstract_1",
                title = "Sacred Mecha Halos",
                style = AnimeStyle.ABSTRACT,
                prompt = "Abstract anime geometric art wallpaper, ethereal glowing neon energy ribbons forming abstract anime mecha wings and sacred geometric halos, deep cosmic indigo background",
                drawableRes = R.drawable.img_anime_abstract,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = false,
                isUpscaled = true,
                upscaleModel = "VectorAI-UltraClear 4x",
                tags = listOf("Abstract", "Geometric", "Mecha Glyphs", "Neon Bloom", "Cosmic"),
                primaryColorHex = "#150E28",
                accentColorHex = "#A855F7",
                description = "Hypnotic geometric anime energy waveforms radiating against obsidian velvet space."
            ),
            // Additional Curated Varieties across the 4 styles (re-using the high-fidelity drawables with distinct curated prompts and styling)
            WallpaperItem(
                id = "anime_cinematic_2",
                title = "Horizon of Twilight",
                style = AnimeStyle.CINEMATIC_ANIME,
                prompt = "Epic Shonen warrior standing at the edge of crystal cliffs looking towards twin moons, glowing runes, cinematic purple sunset",
                drawableRes = R.drawable.img_anime_cinematic,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = false,
                isUpscaled = true,
                tags = listOf("Shonen", "Twin Moons", "Crystal Cliffs", "Epic"),
                primaryColorHex = "#1F1B3E",
                accentColorHex = "#FF5E7E"
            ),
            WallpaperItem(
                id = "anime_watercolor_2",
                title = "Ethereal Spirit Mist",
                style = AnimeStyle.WATERCOLOR,
                prompt = "Ghibli inspired spirit guardian resting beneath glowing wisteria blossoms, soft blue watercolor splashes, delicate brushstrokes",
                drawableRes = R.drawable.img_anime_watercolor,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = false,
                isUpscaled = true,
                tags = listOf("Spirits", "Wisteria", "Ghibli", "Dreamy"),
                primaryColorHex = "#162835",
                accentColorHex = "#06D6A0"
            ),
            WallpaperItem(
                id = "anime_pixel_2",
                title = "Midnight Ramen Bar",
                style = AnimeStyle.PIXEL_ART,
                prompt = "Cozy anime pixel art ramen noodle stall at 2 AM, warm hanging lanterns, steam rising into rainy night, cute cat sleeping on bench",
                drawableRes = R.drawable.img_anime_pixel,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = true,
                isUpscaled = true,
                tags = listOf("Cozy", "Midnight", "Ramen", "Pixel", "Chill"),
                primaryColorHex = "#23182E",
                accentColorHex = "#FFB703"
            ),
            WallpaperItem(
                id = "anime_abstract_2",
                title = "Quantum Speedline Prism",
                style = AnimeStyle.ABSTRACT,
                prompt = "Minimalist abstract anime speedlines collapsing into a prismatic singularity, neon cyan and magenta vector fractals",
                drawableRes = R.drawable.img_anime_abstract,
                resolution = "4K UHD • 3840×2160",
                aspectRatio = "9:16",
                isFavorite = false,
                isUpscaled = true,
                tags = listOf("Speedlines", "Prism", "Singularity", "Minimalist"),
                primaryColorHex = "#0F1426",
                accentColorHex = "#00F0FF"
            )
        )

        wallpaperDao.insertWallpapers(list.map { WallpaperEntity.fromWallpaperItem(it) })
    }
}
