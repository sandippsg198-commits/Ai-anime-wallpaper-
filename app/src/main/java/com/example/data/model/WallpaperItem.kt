package com.example.data.model

data class WallpaperItem(
    val id: String,
    val title: String,
    val style: AnimeStyle,
    val prompt: String,
    val drawableRes: Int? = null,
    val customUri: String? = null,
    val resolution: String = "4K UHD • 3840×2160",
    val aspectRatio: String = "9:16",
    val isFavorite: Boolean = false,
    val isUpscaled: Boolean = true,
    val upscaleModel: String = "AnimeESRGAN-4x v3.2 HDR",
    val tags: List<String> = emptyList(),
    val primaryColorHex: String = "#0D0F1E",
    val accentColorHex: String = "#FF2A85",
    val dateAdded: Long = System.currentTimeMillis(),
    val description: String = ""
)

enum class WallpaperTarget(val displayName: String) {
    HOME_SCREEN("Home Screen"),
    LOCK_SCREEN("Lock Screen"),
    BOTH("Both Screens")
}

enum class AutoChangeInterval(val hours: Long, val displayName: String) {
    HOURLY(1L, "Every 1 Hour"),
    SIX_HOURS(6L, "Every 6 Hours"),
    TWELVE_HOURS(12L, "Every 12 Hours"),
    DAILY(24L, "Daily (Every 24h)")
}

data class AutoChangeSettings(
    val isEnabled: Boolean = false,
    val interval: AutoChangeInterval = AutoChangeInterval.DAILY,
    val target: WallpaperTarget = WallpaperTarget.BOTH,
    val selectedStyleFilter: String = "ALL", // "ALL", "FAVORITES", or AnimeStyle id
    val lastChangedTimestamp: Long = 0L,
    val nextScheduledTimestamp: Long = 0L,
    val totalChangesCount: Int = 0
)
