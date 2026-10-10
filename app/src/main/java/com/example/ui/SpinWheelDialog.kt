package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SpinWheelDialog(
    engine: GameEngine,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonReward by remember { mutableStateOf<String?>(null) }

    val rewards = listOf("৳ 500 Taka", "Kunai × 10", "৳ 1,000 Taka", "Smoke Bomb × 3", "৳ 2,000 Taka", "Armor Plate")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShadowBlack.copy(alpha = 0.9f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 380.dp)
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
                    text = "🎡 DAILY NINJA SPIN WHEEL",
                    color = ElectricGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "One free lucky spin every 24 hours!",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                // Wheel Visual
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .rotate(rotationAnim.value)
                        .background(MidnightBlueLight, CircleShape)
                        .border(4.dp, ElectricGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⭐", fontSize = 48.sp)
                }

                wonReward?.let { reward ->
                    Surface(
                        color = MidnightBlueLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🎉 Won: $reward!",
                            color = ElectricGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (!isSpinning && wonReward == null) {
                            isSpinning = true
                            coroutineScope.launch {
                                val targetAngle = 1440f + Random.nextInt(0, 360)
                                rotationAnim.animateTo(
                                    targetValue = targetAngle,
                                    animationSpec = tween(durationMillis = 2000)
                                )
                                val prize = rewards.random()
                                wonReward = prize
                                isSpinning = false
                                engine.economy.awardMatchEarnings(500, 0, false)
                                engine.audioManager.playKatanaSlash()
                            }
                        }
                    },
                    enabled = !isSpinning && wonReward == null,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("spin_wheel_action_btn")
                ) {
                    Text(if (isSpinning) "SPINNING..." else "SPIN FOR FREE", fontWeight = FontWeight.Black)
                }

                TextButton(onClick = onDismiss) {
                    Text("CLOSE", color = TextSecondary)
                }
            }
        }
    }
}
