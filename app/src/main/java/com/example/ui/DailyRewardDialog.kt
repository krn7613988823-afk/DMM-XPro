package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.example.model.DailyRewardSystem
import com.example.ui.theme.*

@Composable
fun DailyRewardDialog(
    engine: GameEngine,
    onDismiss: () -> Unit
) {
    var claimedDay3 by remember { mutableStateOf(false) }

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
                    text = "📅 7-DAY STREAK LOGIN REWARDS",
                    color = ElectricGold,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Log in daily to claim free Taka and exclusive ninja armory drops!",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DailyRewardSystem.DEFAULT_WEEK.forEach { reward ->
                        val isDay3 = reward.day == 3
                        val isClaimed = reward.isClaimed || (isDay3 && claimedDay3)
                        val isToday = reward.isCurrent && !claimedDay3

                        Surface(
                            color = if (isToday) MidnightBlueLight else MidnightBlueDark,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isToday) ElectricGold else Color.DarkGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Day ${reward.day}", color = if (isToday) ElectricGold else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Column {
                                        Text("৳ ${reward.takaAmount} Taka", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(reward.itemName, color = TextSecondary, fontSize = 10.sp)
                                    }
                                }

                                if (isClaimed) {
                                    Icon(Icons.Default.Check, contentDescription = "Claimed", tint = EmeraldGreen)
                                } else if (isToday) {
                                    Button(
                                        onClick = {
                                            claimedDay3 = true
                                            engine.economy.awardMatchEarnings(reward.takaAmount, 0, false)
                                            engine.audioManager.playKatanaSlash()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("claim_daily_btn")
                                    ) {
                                        Text("CLAIM", fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    }
                                } else {
                                    Text("Locked", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightBlueLight, contentColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE")
                }
            }
        }
    }
}
