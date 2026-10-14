package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpapers ORDER BY dateAdded DESC")
    fun getAllWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE styleId = :styleId ORDER BY dateAdded DESC")
    fun getWallpapersByStyle(styleId: String): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE id = :id LIMIT 1")
    suspend fun getWallpaperById(id: String): WallpaperEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpaper(wallpaper: WallpaperEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpapers(wallpapers: List<WallpaperEntity>)

    @Query("UPDATE wallpapers SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE wallpapers SET isUpscaled = :isUpscaled WHERE id = :id")
    suspend fun updateUpscaled(id: String, isUpscaled: Boolean)

    @Query("DELETE FROM wallpapers WHERE id = :id")
    suspend fun deleteWallpaper(id: String)

    @Query("SELECT COUNT(*) FROM wallpapers")
    suspend fun getCount(): Int

    // Auto change history queries
    @Query("SELECT * FROM auto_wallpaper_history ORDER BY timestamp DESC LIMIT 30")
    fun getAutoChangeHistory(): Flow<List<AutoWallpaperHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryEntry(entry: AutoWallpaperHistoryEntity)

    @Query("DELETE FROM auto_wallpaper_history")
    suspend fun clearHistory()
}
