package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun LobbyScreen(
    engine: GameEngine,
    onStartGame: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenPass: () -> Unit,
    onOpenPet: () -> Unit,
    onOpenGk: () -> Unit,
    onOpenDaily: () -> Unit,
    onOpenClans: () -> Unit,
    onOpenSpin: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var selectedSkin by remember { mutableStateOf(engine.player.activeSkin) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF87CEEB), Color(0xFFE1F5FE), Color(0xFFFFF9C4))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NINJA BATTLE ROYALE",
                        color = ElectricGold,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "SHADOW LEGENDS • NEO DHAKA",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                // Currency & Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Taka Balance
                    Surface(
                        color = MidnightBlueLight,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("৳", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "%,d".format(engine.economy.takaBalance),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Settings Button
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .background(MidnightBlueLight, CircleShape)
                            .testTag("lobby_settings_btn")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = ElectricGold)
                    }
                }
            }

            // Center Character & Companion Showcase
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ninja Card Emblem Showcase
                    Surface(
                        modifier = Modifier
                            .widthIn(max = 380.dp)
                            .fillMaxWidth(0.9f),
                        color = ShadowSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚔ ${selectedSkin.name.uppercase()} ⚔",
                                color = ElectricGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = selectedSkin.description,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Skin Selector Pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                SkinRegistry.ALL_SKINS.take(4).forEach { skin ->
                                    val isSelected = selectedSkin.id == skin.id
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color(skin.primaryColorHex))
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) ElectricGold else Color.Gray,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                selectedSkin = skin
                                                engine.player.activeSkin = skin
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = ElectricGold,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Companion Tag
                            Surface(
                                color = MidnightBlueLight,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🐯", fontSize = 16.sp)
                                    Text(
                                        "${engine.tiger.name} [Lv.${engine.tiger.level}] - ${engine.tiger.evolution.name.replace('_', ' ')}",
                                        color = CyanAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Menu Icons Grid (Infographic Systems)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LobbyNavButton("Shop", "গুলিস্তান", Icons.Default.ShoppingCart, ElectricGold, onOpenShop)
                LobbyNavButton("Pass", "50 Tiers", Icons.Default.Star, CyanAccent, onOpenPass)
                LobbyNavButton("Tiger", "Level 5", Icons.Default.Pets, OrangeFlame, onOpenPet)
                LobbyNavButton("Jutsu", "1000 Qs", Icons.Default.Psychology, EmeraldGreen, onOpenGk)
                LobbyNavButton("Daily", "7 Days", Icons.Default.CalendarToday, ElectricGold, onOpenDaily)
                LobbyNavButton("Clan", "Moholla", Icons.Default.Group, TextPrimary, onOpenClans)
                LobbyNavButton("Spin", "Daily Free", Icons.Default.Casino, CyanAccent, onOpenSpin)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start Match Button
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("play_match_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricGold,
                    contentColor = ShadowBlack
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start Match",
                        modifier = Modifier.size(32.dp),
                        tint = ShadowBlack
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY MATCH (100 NINJAS)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LobbyNavButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .testTag("nav_btn_${title.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(MidnightBlueLight, CircleShape)
                .border(1.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = TextSecondary, fontSize = 9.sp)
    }
}
