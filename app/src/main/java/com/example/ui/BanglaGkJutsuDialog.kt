package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.model.BanglaGkBank
import com.example.model.GkQuestion
import com.example.ui.theme.*

@Composable
fun BanglaGkJutsuDialog(
    engine: GameEngine,
    onDismiss: () -> Unit
) {
    var question by remember { mutableStateOf(BanglaGkBank.getRandomQuestion()) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var answerResult by remember { mutableStateOf<String?>(null) }

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
            border = androidx.compose.foundation.BorderStroke(2.dp, EmeraldGreen)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Text(
                    text = "⚡ BANGLA GK JUTSU ⚡",
                    color = EmeraldGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Answer correctly to instantly charge +50 EP and boost your Golden Katana slash!",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Divider(color = MidnightBlueLight)

                // Bengali Question Card
                Surface(
                    color = MidnightBlueLight,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = question.questionBangla,
                            color = ElectricGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.questionEnglish,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 4 Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    question.options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = index == question.correctIndex
                        val btnColor = when {
                            selectedOptionIndex == null -> MidnightBlueLight
                            isCorrect -> EmeraldGreen.copy(alpha = 0.3f)
                            isSelected -> CrimsonAlert.copy(alpha = 0.3f)
                            else -> MidnightBlueLight
                        }
                        val borderColor = when {
                            selectedOptionIndex == null -> CyanAccent.copy(alpha = 0.5f)
                            isCorrect -> EmeraldGreen
                            isSelected -> CrimsonAlert
                            else -> Color.Gray
                        }

                        Surface(
                            onClick = {
                                if (selectedOptionIndex == null) {
                                    selectedOptionIndex = index
                                    if (isCorrect) {
                                        answerResult = "Correct! +50 EP restored & Golden Jutsu Charged!"
                                        engine.player.energyPoints = (engine.player.energyPoints + 50f).coerceAtMost(engine.player.maxEnergyPoints)
                                        engine.economy.awardMatchEarnings(100, 0, false)
                                        engine.audioManager.playKatanaSlash()
                                    } else {
                                        answerResult = "Incorrect! ${question.trivia}"
                                        engine.audioManager.playHitImpact()
                                    }
                                }
                            },
                            color = btnColor,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth().testTag("gk_option_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + index)}. ",
                                    color = ElectricGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = optionText,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                answerResult?.let { msg ->
                    Text(
                        text = msg,
                        color = if (selectedOptionIndex == question.correctIndex) EmeraldGreen else CrimsonAlert,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE JUTSU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
