package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.ui.theme.*

@Composable
fun SettingsDialog(
    engine: GameEngine,
    onDismiss: () -> Unit
) {
    var sensitivity by remember { mutableFloatStateOf(engine.camera.aimSensitivity) }
    var soundOn by remember { mutableStateOf(engine.audioManager.isSoundEnabled) }
    var vibrationOn by remember { mutableStateOf(engine.audioManager.isVibrationEnabled) }
    var selectedGraphics by remember { mutableStateOf("High (60 FPS)") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShadowBlack.copy(alpha = 0.9f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(0.9f)
                .clickable(enabled = false) {},
            color = MidnightBlueDark,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⚙️ GAME SETTINGS",
                    color = ElectricGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                // Graphics Preset
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Graphics Quality & Target FPS", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Low (30FPS)", "Med (60FPS)", "High (60 FPS)").forEach { mode ->
                            val isSelected = selectedGraphics == mode
                            Surface(
                                onClick = { selectedGraphics = mode },
                                color = if (isSelected) ElectricGold else MidnightBlueLight,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = mode,
                                    color = if (isSelected) ShadowBlack else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Aim Sensitivity Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Aim Sensitivity", color = TextSecondary, fontSize = 12.sp)
                        Text("${"%.1f".format(sensitivity)}x", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                        value = sensitivity,
                        onValueChange = {
                            sensitivity = it
                            engine.camera.aimSensitivity = it
                        },
                        valueRange = 0.5f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricGold,
                            activeTrackColor = ElectricGold,
                            inactiveTrackColor = MidnightBlueLight
                        )
                    )
                }

                // Audio Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects & Audio", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = soundOn,
                        onCheckedChange = {
                            soundOn = it
                            engine.audioManager.isSoundEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricGold, checkedTrackColor = ElectricGoldDark)
                    )
                }

                // Vibration Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mobile Vibration Haptics", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = vibrationOn,
                        onCheckedChange = {
                            vibrationOn = it
                            engine.audioManager.isVibrationEnabled = it
                            if (it) engine.audioManager.vibrate(50, 200)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricGold, checkedTrackColor = ElectricGoldDark)
                    )
                }

                // Custom HUD Button (Free Fire Style)
                var showHudEditor by remember { mutableStateOf(false) }
                val context = androidx.compose.ui.platform.LocalContext.current

                Button(
                    onClick = { showHudEditor = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800), contentColor = ShadowBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_custom_hud_btn")
                ) {
                    Text("🎨 CUSTOM HUD (FREE FIRE STYLE)", fontWeight = FontWeight.Black)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("close_settings_btn")
                ) {
                    Text("APPLY & CLOSE", fontWeight = FontWeight.Black)
                }

                if (showHudEditor) {
                    CustomHudEditorDialog(
                        currentConfig = com.example.model.CustomHudConfig.load(context),
                        onSaveAndExit = { saved ->
                            saved.save(context)
                            showHudEditor = false
                        },
                        onCancel = { showHudEditor = false }
                    )
                }
            }
        }
    }
}
