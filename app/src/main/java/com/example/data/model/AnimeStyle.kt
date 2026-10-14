package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StyleAbstractColor
import com.example.ui.theme.StyleCinematicColor
import com.example.ui.theme.StylePixelColor
import com.example.ui.theme.StyleWatercolorColor

enum class AnimeStyle(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val accentColor: Color,
    val tagKeywords: List<String>,
    val promptModifier: String,
    val fidelityDescription: String
) {
    CINEMATIC_ANIME(
        id = "cinematic",
        displayName = "Cinematic Anime",
        subtitle = "Makoto Shinkai & Ufotable Glow",
        accentColor = StyleCinematicColor,
        tagKeywords = listOf("8K Scenery", "Cel Shaded", "Volumetric Light", "Makoto Shinkai", "Shonen"),
        promptModifier = "cinematic high-budget anime scenery, ultra-detailed line art, vibrant atmospheric lighting, dramatic skies, 8k wallpaper, anime masterpiece",
        fidelityDescription = "Crisp razor-sharp line art with raytraced atmospheric volumetric light and rich color depth."
    ),
    WATERCOLOR(
        id = "watercolor",
        displayName = "Watercolor Anime",
        subtitle = "Delicate Washes & Fluid Pigments",
        accentColor = StyleWatercolorColor,
        tagKeywords = listOf("Hand Painted", "Pastel Pigment", "Paper Texture", "Ghibli Mood", "Fluid Art"),
        promptModifier = "delicate anime watercolor illustration, soft pigment bleeding, traditional cold-press paper grain texture, ethereal translucent layers, serene pastel tones",
        fidelityDescription = "Authentic cold-press paper grain texture with fluid color bleeds and airy brushstroke fidelity."
    ),
    PIXEL_ART(
        id = "pixel_art",
        displayName = "16-Bit Pixel Art",
        subtitle = "Retro Cyberpunk & Lo-Fi Nostalgia",
        accentColor = StylePixelColor,
        tagKeywords = listOf("16-Bit", "Chiptune Vibe", "Dithered Shadows", "Neo-Tokyo", "Lo-Fi"),
        promptModifier = "retro 16-bit cyber anime pixel art, intricate dithered shadows, glowing neon signage, nostalgic lo-fi arcade color palette, pixel perfection, clean crisp pixel scaling",
        fidelityDescription = "Integer-scaled pixel perfection with hand-placed dithered highlights and retro CRT neon reflections."
    ),
    ABSTRACT(
        id = "abstract",
        displayName = "Abstract Anime",
        subtitle = "Geometric Halos & Energy Ribbons",
        accentColor = StyleAbstractColor,
        tagKeywords = listOf("Geometric Halos", "Mecha Glyphs", "Kinetic Energy", "Cosmic Speedlines", "Minimalist"),
        promptModifier = "abstract anime energy ribbons and sacred geometric mecha halos, glowing holographic cyber glyphs, cosmic deep space minimalist backdrop, sleek futuristic vector fidelity",
        fidelityDescription = "Sub-pixel vector smooth energy curves with high dynamic range neon bloom against obsidian blacks."
    );

    companion object {
        fun fromId(id: String): AnimeStyle {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CINEMATIC_ANIME
        }
    }
}
