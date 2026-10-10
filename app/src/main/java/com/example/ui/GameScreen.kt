package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.*
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun GameScreen(
    engine: GameEngine,
    onReturnToLobby: () -> Unit,
    onOpenGkJutsu: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var hudConfig by remember { mutableStateOf(CustomHudConfig.load(context)) }

    var lastFrameNanos by remember { mutableLongStateOf(System.nanoTime()) }
    var moveJoystickX by remember { mutableFloatStateOf(0f) }
    var moveJoystickY by remember { mutableFloatStateOf(0f) }
    var renderTick by remember { mutableIntStateOf(0) }

    val renderer = remember { Renderer3D() }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { now ->
                val dt = ((now - lastFrameNanos) / 1_000_000_000f).coerceIn(0.005f, 0.05f)
                lastFrameNanos = now

                engine.update(dt, moveJoystickX, moveJoystickY)
                renderTick++
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Bright Day Sky
    ) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        // 1. 3D Game Canvas Viewport
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("game_viewport_canvas")
        ) {
            val tick = renderTick
            renderer.renderScene(
                scope = this,
                camera = engine.camera,
                player = engine.player,
                bots = engine.bots,
                tiger = engine.tiger,
                safeZone = engine.safeZone,
                buildings = engine.buildings,
                crates = engine.lootCrates,
                groundLoot = engine.groundLootItems,
                jumpPads = engine.jumpPads,
                projectiles = engine.projectiles,
                slashes = engine.slashes,
                floatingTexts = engine.floatingDamageTexts,
                animTimeSec = engine.animTimeSec
            )
        }

        // 2. Right Half Touch Area for Free Look / Camera Aiming
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.55f)
                .align(Alignment.CenterEnd)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        engine.camera.rotate(
                            deltaYaw = dragAmount.x * 0.35f,
                            deltaPitch = dragAmount.y * 0.25f
                        )
                    }
                }
        )

        // 3. TOP BARS (Small, Compact, Completely Off Center - Ninja 80% Unobstructed!)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(8.dp)
        ) {
            // TOP-LEFT: Compact Health & Armor Bars (80dp x 12dp)
            CompactStatusBars(
                player = engine.player,
                modifier = Modifier.align(Alignment.TopStart)
            )

            // TOP-CENTER: Small black background pill with Location & Safe Zone (12sp)
            Surface(
                modifier = Modifier.align(Alignment.TopCenter),
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📍 Clock Tower", color = ElectricGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("•", color = Color.Gray, fontSize = 12.sp)
                    Text("Zone Phase ${engine.safeZone.phase}: ${engine.safeZone.timerSeconds.toInt()}s", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // TOP-RIGHT: Small ALIVE & KILL (14sp) and Settings Button
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ALIVE ", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("${engine.alivePlayersCount}", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("KILL ", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("${engine.playerKills}", color = CrimsonAlert, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.85f), CircleShape)
                        .border(1.dp, ElectricGold, CircleShape)
                        .testTag("game_settings_btn")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = ElectricGold, modifier = Modifier.size(16.dp))
                }
            }
        }

        // 4. FREE FIRE CUSTOM HUD BUTTONS (Positioned & Scaled according to CustomHudConfig)
        // A. Dynamic Virtual Joystick
        val joyX = (hudConfig.joystick.xPercent * screenW) - (hudConfig.joystick.sizeDp / 2f)
        val joyY = (hudConfig.joystick.yPercent * screenH) - (hudConfig.joystick.sizeDp / 2f)
        Box(
            modifier = Modifier
                .offset { IntOffset(joyX.dp.roundToPx(), joyY.dp.roundToPx()) }
                .size(hudConfig.joystick.sizeDp.dp)
                .alpha(hudConfig.joystick.alpha)
        ) {
            VirtualJoystickComponent(
                sizeDp = hudConfig.joystick.sizeDp,
                onMove = { x, y ->
                    moveJoystickX = x
                    moveJoystickY = y
                }
            )
        }

        // B. Dynamic Primary Attack / Fire Button (With Ammo Display)
        val fireX = (hudConfig.fireBtn.xPercent * screenW) - (hudConfig.fireBtn.sizeDp / 2f)
        val fireY = (hudConfig.fireBtn.yPercent * screenH) - (hudConfig.fireBtn.sizeDp / 2f)
        Box(
            modifier = Modifier
                .offset { IntOffset(fireX.dp.roundToPx(), fireY.dp.roundToPx()) }
                .size(hudConfig.fireBtn.sizeDp.dp)
                .alpha(hudConfig.fireBtn.alpha)
        ) {
            Button(
                onClick = { engine.triggerAttack() },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("primary_attack_btn"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack),
                contentPadding = PaddingValues(0.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Fire", modifier = Modifier.size((hudConfig.fireBtn.sizeDp * 0.42f).dp), tint = Color.Black)
                    Text(
                        "${engine.player.currentAmmoInMag}/20",
                        color = Color.Black,
                        fontSize = (hudConfig.fireBtn.sizeDp * 0.12f).sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // C. Jump Button
        CustomHudButton(
            cfg = hudConfig.jumpBtn,
            screenW = screenW,
            screenH = screenH,
            onClick = { engine.triggerJump() },
            bgColor = Color.White,
            borderColor = Color(0xFF0091EA)
        ) {
            Icon(Icons.Default.ArrowUpward, "Jump", tint = Color(0xFF0091EA), modifier = Modifier.size(28.dp))
        }

        // D. Shadow Dash Button
        CustomHudButton(
            cfg = hudConfig.dashBtn,
            screenW = screenW,
            screenH = screenH,
            onClick = { engine.triggerShadowDash() },
            bgColor = ElectricGold,
            borderColor = Color.Black
        ) {
            Icon(Icons.Default.Bolt, "Dash", tint = Color.Black, modifier = Modifier.size(30.dp))
        }

        // E. Crouch Button
        CustomHudButton(
            cfg = hudConfig.crouchBtn,
            screenW = screenW,
            screenH = screenH,
            onClick = { engine.toggleCrouch() },
            bgColor = if (engine.player.isCrouching) ElectricGold else Color.White,
            borderColor = ElectricGold
        ) {
            Text("🔻", fontSize = 18.sp)
        }

        // F. Block Guard Button
        CustomHudButton(
            cfg = hudConfig.blockBtn,
            screenW = screenW,
            screenH = screenH,
            onClick = { engine.toggleBlock(!engine.player.isBlocking) },
            bgColor = if (engine.player.isBlocking) Color(0xFF00E5FF) else Color.White,
            borderColor = Color(0xFF0091EA)
        ) {
            Icon(Icons.Default.Shield, "Block", tint = if (engine.player.isBlocking) Color.Black else Color(0xFF0091EA), modifier = Modifier.size(24.dp))
        }

        // G. Reload Button
        CustomHudButton(
            cfg = hudConfig.reloadBtn,
            screenW = screenW,
            screenH = screenH,
            onClick = { engine.triggerReload() },
            bgColor = if (engine.player.isReloading) Color(0xFFFFCC80) else Color.White,
            borderColor = ElectricGold
        ) {
            Icon(Icons.Default.Refresh, "Reload", tint = Color.Black, modifier = Modifier.size(22.dp))
        }

        // H. 4x Scope Button
        CustomHudButton(
            cfg = hudConfig.scopeBtn,
            screenW = screenW,
            screenH = screenH,
            onClick = { engine.toggleScope4x() },
            bgColor = if (engine.player.isScoping4x) Color(0xFF00E5FF) else Color.White,
            borderColor = Color(0xFF0091EA)
        ) {
            Icon(Icons.Default.GpsFixed, "Scope 4x", tint = Color.Black, modifier = Modifier.size(24.dp))
        }

        // I. Medkit Button
        CustomHudButton(
            cfg = hudConfig.medkitBtn,
            screenW = screenW,
            screenH = screenH,
            onClick = { engine.useMedkit() },
            bgColor = Color.White,
            borderColor = Color(0xFF2E7D32)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Healing, "Medkit", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                Text("×${engine.player.medkitCount}", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        // Bottom Center Weapon Switcher
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                WeaponRegistry.AK_BD,
                WeaponRegistry.GOLD_KATANA,
                WeaponRegistry.AWM_GOLD,
                WeaponRegistry.GOLD_KATANA_ULTIMATE
            ).forEach { weapon ->
                val isEquipped = engine.player.equippedWeapon.id == weapon.id
                Surface(
                    onClick = { engine.equipWeapon(weapon) },
                    color = if (isEquipped) ElectricGold else Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(if (isEquipped) 2.5.dp else 1.dp, if (isEquipped) Color(0xFF1A1A1A) else Color.LightGray),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(if (weapon.category == WeaponCategory.FIREARM) "🔫" else "⚔", fontSize = 12.sp)
                        Text(weapon.name, color = Color(0xFF1A1A1A), fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }

            Button(
                onClick = onOpenGkJutsu,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = ShadowBlack),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp).testTag("bangla_gk_jutsu_btn")
            ) {
                Text("⚡ JUTSU", fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }

        // 5. Booyah Victory & Defeat Overlays
        if (engine.matchState == MatchState.VICTORY_BOOYAH) {
            VictoryBooyahDialog(
                kills = engine.playerKills,
                timeSec = engine.survivalTimerSec.toInt(),
                onReturnToLobby = onReturnToLobby
            )
        } else if (engine.matchState == MatchState.DEFEAT) {
            DefeatDialog(
                kills = engine.playerKills,
                timeSec = engine.survivalTimerSec.toInt(),
                onRetry = { engine.initMatch() },
                onReturnToLobby = onReturnToLobby
            )
        }
    }
}

@Composable
private fun CustomHudButton(
    cfg: HudElementConfig,
    screenW: Float,
    screenH: Float,
    onClick: () -> Unit,
    bgColor: Color,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    val x = (cfg.xPercent * screenW) - (cfg.sizeDp / 2f)
    val y = (cfg.yPercent * screenH) - (cfg.sizeDp / 2f)

    Box(
        modifier = Modifier
            .offset { IntOffset(x.dp.roundToPx(), y.dp.roundToPx()) }
            .size(cfg.sizeDp.dp)
            .alpha(cfg.alpha)
            .clip(CircleShape)
            .background(bgColor)
            .border(2.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun CompactStatusBars(player: CharacterEntity, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.width(130.dp),
        color = Color.Black.copy(alpha = 0.85f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.5.dp)
        ) {
            // HP Bar (80dp x 8dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("HP", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1B5E20))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(player.health / player.maxHealth)
                            .background(Color(0xFF00E676))
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("${player.health.toInt()}", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }

            // EP Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("EP", color = ElectricGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF423800))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(player.energyPoints / player.maxEnergyPoints)
                            .background(ElectricGold)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("${player.energyPoints.toInt()}", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }

            // AP Armor Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("AP", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF003847))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(player.armor / player.maxArmor)
                            .background(Color(0xFF00E5FF))
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("${player.armor.toInt()}", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VirtualJoystickComponent(sizeDp: Float, onMove: (Float, Float) -> Unit) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadius = sizeDp * 0.38f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.85f))
            .border(3.dp, ElectricGold, CircleShape)
            .pointerInput(sizeDp) {
                detectDragGestures(
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val newOffset = thumbOffset + dragAmount
                    val dist = newOffset.getDistance()
                    thumbOffset = if (dist > maxRadius) {
                        newOffset * (maxRadius / dist)
                    } else {
                        newOffset
                    }
                    onMove(thumbOffset.x / maxRadius, -thumbOffset.y / maxRadius)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val knobSize = (sizeDp * 0.42f).dp
        Box(
            modifier = Modifier
                .offset(thumbOffset.x.dp / 2.7f, thumbOffset.y.dp / 2.7f)
                .size(knobSize)
                .clip(CircleShape)
                .background(ElectricGold)
                .border(2.5.dp, Color(0xFF212121), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("⚡", fontSize = (sizeDp * 0.16f).sp)
        }
    }
}

@Composable
private fun VictoryBooyahDialog(
    kills: Int,
    timeSec: Int,
    onReturnToLobby: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(0.85f),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(4.dp, ElectricGold)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🏆 BOOYAH! 🏆",
                    color = Color(0xFFD4AF37),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "#1 SHADOW LEGEND OF NEO DHAKA",
                    color = Color(0xFF0091EA),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = ElectricGold, thickness = 1.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Eliminations", color = Color.Gray, fontSize = 13.sp)
                        Text("$kills", color = CrimsonAlert, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Survival Time", color = Color.Gray, fontSize = 13.sp)
                        Text("${timeSec / 60}m ${timeSec % 60}s", color = Color(0xFFD4AF37), fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                }

                Surface(
                    color = Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Match Reward: ৳ 2,500 Taka Earned!",
                        color = Color(0xFFF57F17),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Button(
                    onClick = onReturnToLobby,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack)
                ) {
                    Text("RETURN TO LOBBY", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun DefeatDialog(
    kills: Int,
    timeSec: Int,
    onRetry: () -> Unit,
    onReturnToLobby: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(0.85f),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(3.dp, CrimsonAlert)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "ELIMINATED",
                    color = CrimsonAlert,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Better luck next drop in Bermuda!",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Eliminations", color = Color.Gray, fontSize = 13.sp)
                        Text("$kills", color = Color(0xFF212121), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Survival Time", color = Color.Gray, fontSize = 13.sp)
                        Text("${timeSec / 60}m ${timeSec % 60}s", color = Color(0xFFD4AF37), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReturnToLobby,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("LOBBY", color = Color(0xFF212121))
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack)
                    ) {
                        Text("RETRY", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
