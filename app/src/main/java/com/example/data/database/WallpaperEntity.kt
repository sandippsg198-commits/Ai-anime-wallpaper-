package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AnimeStyle
import com.example.data.model.WallpaperItem

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey val id: String,
    val title: String,
    val styleId: String,
    val prompt: String,
    val drawableRes: Int?,
    val customUri: String?,
    val resolution: String,
    val aspectRatio: String,
    val isFavorite: Boolean,
    val isUpscaled: Boolean,
    val upscaleModel: String,
    val tagsCommaSeparated: String,
    val primaryColorHex: String,
    val accentColorHex: String,
    val dateAdded: Long,
    val description: String
) {
    fun toWallpaperItem(): WallpaperItem {
        return WallpaperItem(
            id = id,
            title = title,
            style = AnimeStyle.fromId(styleId),
            prompt = prompt,
            drawableRes = drawableRes,
            customUri = customUri,
            resolution = resolution,
            aspectRatio = aspectRatio,
            isFavorite = isFavorite,
            isUpscaled = isUpscaled,
            upscaleModel = upscaleModel,
            tags = if (tagsCommaSeparated.isBlank()) emptyList() else tagsCommaSeparated.split(","),
            primaryColorHex = primaryColorHex,
            accentColorHex = accentColorHex,
            dateAdded = dateAdded,
            description = description
        )
    }

    companion object {
        fun fromWallpaperItem(item: WallpaperItem): WallpaperEntity {
            return WallpaperEntity(
                id = item.id,
                title = item.title,
                styleId = item.style.id,
                prompt = item.prompt,
                drawableRes = item.drawableRes,
                customUri = item.customUri,
                resolution = item.resolution,
                aspectRatio = item.aspectRatio,
                isFavorite = item.isFavorite,
                isUpscaled = item.isUpscaled,
                upscaleModel = item.upscaleModel,
                tagsCommaSeparated = item.tags.joinToString(","),
                primaryColorHex = item.primaryColorHex,
                accentColorHex = item.accentColorHex,
                dateAdded = item.dateAdded,
                description = item.description
            )
        }
    }
}

@Entity(tableName = "auto_wallpaper_history")
data class AutoWallpaperHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val wallpaperId: String,
    val wallpaperTitle: String,
    val styleId: String,
    val targetScreen: String,
    val timestamp: Long = System.currentTimeMillis()
)
