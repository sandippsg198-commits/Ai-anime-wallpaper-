package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.WallpaperEntity
import com.example.data.model.AnimeStyle
import com.example.data.model.WallpaperItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AnimAI Wallpapers", appName)
  }

  @Test
  fun `verify diverse artistic anime styles exist`() {
    val styles = AnimeStyle.entries
    assertTrue(styles.any { it == AnimeStyle.CINEMATIC_ANIME })
    assertTrue(styles.any { it == AnimeStyle.WATERCOLOR })
    assertTrue(styles.any { it == AnimeStyle.PIXEL_ART })
    assertTrue(styles.any { it == AnimeStyle.ABSTRACT })
  }

  @Test
  fun `verify wallpaper entity conversion`() {
    val item = WallpaperItem(
      id = "test_1",
      title = "Test Watercolor Artwork",
      style = AnimeStyle.WATERCOLOR,
      prompt = "peaceful sakura garden",
      resolution = "4K UHD • 3840×2160"
    )
    val entity = WallpaperEntity.fromWallpaperItem(item)
    val converted = entity.toWallpaperItem()

    assertEquals(item.id, converted.id)
    assertEquals(item.style, converted.style)
    assertEquals(item.resolution, converted.resolution)
  }
}
