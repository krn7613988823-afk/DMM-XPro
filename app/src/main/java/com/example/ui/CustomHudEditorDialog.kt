package com.example.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomHudConfig
import com.example.model.HudElementConfig
import com.example.ui.theme.ElectricGold
import kotlin.math.roundToInt

@Composable
fun CustomHudEditorDialog(
    currentConfig: CustomHudConfig,
    onSaveAndExit: (CustomHudConfig) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var selectedElementKey by remember { mutableStateOf("fire") }

    // Local mutable copy of configs
    var joystickCfg by remember { mutableStateOf(currentConfig.joystick.copy()) }
    var fireCfg by remember { mutableStateOf(currentConfig.fireBtn.copy()) }
    var jumpCfg by remember { mutableStateOf(currentConfig.jumpBtn.copy()) }
    var dashCfg by remember { mutableStateOf(currentConfig.dashBtn.copy()) }
    var crouchCfg by remember { mutableStateOf(currentConfig.crouchBtn.copy()) }
    var blockCfg by remember { mutableStateOf(currentConfig.blockBtn.copy()) }
    var reloadCfg by remember { mutableStateOf(currentConfig.reloadBtn.copy()) }
    var scopeCfg by remember { mutableStateOf(currentConfig.scopeBtn.copy()) }
    var medkitCfg by remember { mutableStateOf(currentConfig.medkitBtn.copy()) }

    val activeElem = when (selectedElementKey) {
        "joystick" -> joystickCfg
        "fire" -> fireCfg
        "jump" -> jumpCfg
        "dash" -> dashCfg
        "crouch" -> crouchCfg
        "block" -> blockCfg
        "reload" -> reloadCfg
        "scope" -> scopeCfg
        "medkit" -> medkitCfg
        else -> fireCfg
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xD0101824))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        // Helper to render draggable HUD button in editor
        @Composable
        fun DraggableHudButton(
            key: String,
            cfg: HudElementConfig,
            label: String,
            icon: @Composable () -> Unit,
            onConfigChange: (HudElementConfig) -> Unit
        ) {
            val isSelected = selectedElementKey == key
            val posX = (cfg.xPercent * screenW) - (cfg.sizeDp / 2f)
            val posY = (cfg.yPercent * screenH) - (cfg.sizeDp / 2f)

            Box(
                modifier = Modifier
                    .offset { IntOffset(posX.dp.roundToPx(), posY.dp.roundToPx()) }
                    .size(cfg.sizeDp.dp)
                    .alpha(cfg.alpha)
                    .clip(CircleShape)
                    .background(if (isSelected) ElectricGold else Color.White.copy(alpha = 0.85f))
                    .border(
                        width = if (isSelected) 3.5.dp else 1.5.dp,
                        color = if (isSelected) Color(0xFFE53935) else ElectricGold,
                        shape = CircleShape
                    )
                    .clickable { selectedElementKey = key }
                    .pointerInput(key) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newXPercent = ((cfg.xPercent * screenW + dragAmount.x) / screenW).coerceIn(0.05f, 0.95f)
                            val newYPercent = ((cfg.yPercent * screenH + dragAmount.y) / screenH).coerceIn(0.1f, 0.92f)
                            onConfigChange(cfg.copy(xPercent = newXPercent, yPercent = newYPercent))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    icon()
                    Text(label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }

        // Render all HUD buttons
        DraggableHudButton("joystick", joystickCfg, "JOYSTICK", { Icon(Icons.Default.SportsEsports, "Joy", tint = Color.Black) }) { joystickCfg = it }
        DraggableHudButton("fire", fireCfg, "FIRE", { Icon(Icons.Default.FlashOn, "Fire", tint = Color.Black) }) { fireCfg = it }
        DraggableHudButton("jump", jumpCfg, "JUMP", { Icon(Icons.Default.ArrowUpward, "Jump", tint = Color.Black) }) { jumpCfg = it }
        DraggableHudButton("dash", dashCfg, "DASH", { Icon(Icons.Default.Bolt, "Dash", tint = Color.Black) }) { dashCfg = it }
        DraggableHudButton("crouch", crouchCfg, "CROUCH", { Text("🔻", fontSize = 16.sp) }) { crouchCfg = it }
        DraggableHudButton("block", blockCfg, "BLOCK", { Icon(Icons.Default.Shield, "Block", tint = Color.Black) }) { blockCfg = it }
        DraggableHudButton("reload", reloadCfg, "RELOAD", { Icon(Icons.Default.Refresh, "Reload", tint = Color.Black) }) { reloadCfg = it }
        DraggableHudButton("scope", scopeCfg, "SCOPE 4X", { Icon(Icons.Default.GpsFixed, "Scope", tint = Color.Black) }) { scopeCfg = it }
        DraggableHudButton("medkit", medkitCfg, "MEDKIT", { Icon(Icons.Default.Healing, "Medkit", tint = Color(0xFF2E7D32)) }) { medkitCfg = it }

        // Top Control Bar & Inspector
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.92f)
                .padding(top = 8.dp),
            color = Color.White.copy(alpha = 0.96f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚙️ FREE FIRE CUSTOM HUD • EDITING [${selectedElementKey.uppercase()}]",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color(0xFF1A1A1A)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val def = CustomHudConfig.default()
                                joystickCfg = def.joystick
                                fireCfg = def.fireBtn
                                jumpCfg = def.jumpBtn
                                dashCfg = def.dashBtn
                                crouchCfg = def.crouchBtn
                                blockCfg = def.blockBtn
                                reloadCfg = def.reloadBtn
                                scopeCfg = def.scopeBtn
                                medkitCfg = def.medkitBtn
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC80), contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("RESET DEFAULT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val newCfg = CustomHudConfig(
                                    joystick = joystickCfg,
                                    fireBtn = fireCfg,
                                    jumpBtn = jumpCfg,
                                    dashBtn = dashCfg,
                                    crouchBtn = crouchCfg,
                                    blockBtn = blockCfg,
                                    reloadBtn = reloadCfg,
                                    scopeBtn = scopeCfg,
                                    medkitBtn = medkitCfg
                                )
                                newCfg.save(context)
                                onSaveAndExit(newCfg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("save_custom_hud_btn")
                        ) {
                            Text("SAVE LAYOUT", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                        }
                    }
                }

                // Sliders Row: Size (50dp to 200dp) and Opacity (30% to 100%)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Size Slider
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Size: ${activeElem.sizeDp.toInt()}dp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Slider(
                            value = activeElem.sizeDp,
                            onValueChange = { newSize ->
                                when (selectedElementKey) {
                                    "joystick" -> joystickCfg = joystickCfg.copy(sizeDp = newSize)
                                    "fire" -> fireCfg = fireCfg.copy(sizeDp = newSize)
                                    "jump" -> jumpCfg = jumpCfg.copy(sizeDp = newSize)
                                    "dash" -> dashCfg = dashCfg.copy(sizeDp = newSize)
                                    "crouch" -> crouchCfg = crouchCfg.copy(sizeDp = newSize)
                                    "block" -> blockCfg = blockCfg.copy(sizeDp = newSize)
                                    "reload" -> reloadCfg = reloadCfg.copy(sizeDp = newSize)
                                    "scope" -> scopeCfg = scopeCfg.copy(sizeDp = newSize)
                                    "medkit" -> medkitCfg = medkitCfg.copy(sizeDp = newSize)
                                }
                            },
                            valueRange = 50f..200f,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Opacity Slider
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Opacity: ${(activeElem.alpha * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Slider(
                            value = activeElem.alpha,
                            onValueChange = { newAlpha ->
                                when (selectedElementKey) {
                                    "joystick" -> joystickCfg = joystickCfg.copy(alpha = newAlpha)
                                    "fire" -> fireCfg = fireCfg.copy(alpha = newAlpha)
                                    "jump" -> jumpCfg = jumpCfg.copy(alpha = newAlpha)
                                    "dash" -> dashCfg = dashCfg.copy(alpha = newAlpha)
                                    "crouch" -> crouchCfg = crouchCfg.copy(alpha = newAlpha)
                                    "block" -> blockCfg = blockCfg.copy(alpha = newAlpha)
                                    "reload" -> reloadCfg = reloadCfg.copy(alpha = newAlpha)
                                    "scope" -> scopeCfg = scopeCfg.copy(alpha = newAlpha)
                                    "medkit" -> medkitCfg = medkitCfg.copy(alpha = newAlpha)
                                }
                            },
                            valueRange = 0.3f..1.0f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
