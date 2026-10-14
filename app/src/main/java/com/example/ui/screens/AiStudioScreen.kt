package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimeStyle
import com.example.ui.theme.AnimeDarkBackground
import com.example.ui.theme.AnimeSurfaceCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AiGenerationStatus

@Composable
fun AiStudioScreen(
    currentStyle: AnimeStyle,
    onStyleSelected: (AnimeStyle) -> Unit,
    generationStatus: AiGenerationStatus,
    onGenerate: (AnimeStyle, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf("Cyber Samurai Maiden") }
    var selectedAtmosphere by remember { mutableStateOf("Neon Cyber Rain") }
    var selectedLighting by remember { mutableStateOf("Volumetric Cel-Shading") }
    var customPromptExtra by remember { mutableStateOf("") }

    val subjects = listOf(
        "Cyber Samurai Maiden",
        "Kitsune Spirit Guardian",
        "Valkyrie Cosmic Mecha",
        "Lo-Fi Rainy City Cafe",
        "Celestial Dragon Rider",
        "Cherry Blossom Shrine",
        "Twin Moons Nomad"
    )

    val atmospheres = listOf(
        "Neon Cyber Rain",
        "Sun-Dappled Sakura",
        "Deep Space Nebula",
        "Golden Hour Twilight",
        "Moody Midnight Mist",
        "Prismatic Crystal Glow"
    )

    val lightings = listOf(
        "Volumetric Cel-Shading",
        "Soft Watercolor Wash",
        "Retro CRT 16-Bit Glow",
        "Radiant Neon Vector",
        "Bioluminescent Embers"
    )

    val isGenerating = generationStatus is AiGenerationStatus.Generating

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_studio_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonPink,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Anime Studio",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimary
                        )
                    }

                    // Randomize Inspiration button
                    OutlinedButton(
                        onClick = {
                            selectedSubject = subjects.random()
                            selectedAtmosphere = atmospheres.random()
                            selectedLighting = lightings.random()
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.testTag("randomize_prompt_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Randomize",
                            tint = GoldGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Surprise Me", color = TextPrimary, fontSize = 12.sp)
                    }
                }
                Text(
                    text = "Generate high-fidelity wallpapers in multiple distinct anime art styles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // 1. Style Selection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Choose Artistic Style",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Surface(
                            color = currentStyle.accentColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, currentStyle.accentColor)
                        ) {
                            Text(
                                text = currentStyle.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = currentStyle.accentColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // 4 Style Selector Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimeStyle.entries.forEach { style ->
                            val isSelected = currentStyle == style
                            FilterChip(
                                selected = isSelected,
                                onClick = { onStyleSelected(style) },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(style.accentColor)
                                        )
                                        Text(style.displayName)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = style.accentColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = BorderSubtle,
                                    selectedBorderColor = style.accentColor
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("ai_studio_style_${style.id}")
                            )
                        }
                    }

                    // Style description note
                    Text(
                        text = currentStyle.fidelityDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier
                            .background(AnimeDarkBackground, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        // 2. Character / Subject
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "2. Anime Subject & Character",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subjects.forEach { subject ->
                            val isSelected = selectedSubject == subject
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSubject = subject },
                                label = { Text(subject) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CosmicPurple,
                                    selectedLabelColor = Color.White,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Atmosphere & Environment
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3. Atmosphere & Lighting",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Text(
                        text = "Atmosphere",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        atmospheres.forEach { atmosphere ->
                            val isSelected = selectedAtmosphere == atmosphere
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedAtmosphere = atmosphere },
                                label = { Text(atmosphere) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = AnimeDarkBackground,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "Lighting Palette",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        lightings.forEach { light ->
                            val isSelected = selectedLighting == light
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedLighting = light },
                                label = { Text(light) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldGlow,
                                    selectedLabelColor = AnimeDarkBackground,
                                    containerColor = AnimeDarkBackground,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Custom Details & Direct Prompt Input
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "4. Additional Prompt Details (Optional)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = customPromptExtra,
                        onValueChange = { customPromptExtra = it },
                        placeholder = { Text("e.g. golden hair, katana glowing with blue lightning, wearing earphones...", color = TextMuted) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AnimeDarkBackground,
                            unfocusedContainerColor = AnimeDarkBackground,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("custom_prompt_input")
                    )
                }
            }
        }

        // 5. Generate Button & Status
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onGenerate(
                            currentStyle,
                            selectedSubject,
                            selectedAtmosphere,
                            selectedLighting,
                            customPromptExtra
                        )
                    },
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("generate_wallpaper_btn")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Synthesizing ${currentStyle.displayName} (4K Upscaling)...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generate 4K Wallpaper",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Engine Specifications badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Multi-Style Anime Diffusion • 4x ESRGAN HDR Super-Resolution",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
