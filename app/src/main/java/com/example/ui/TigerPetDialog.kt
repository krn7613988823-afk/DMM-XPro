package com.example.ui

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.model.PetState
import com.example.model.TigerEvolution
import com.example.ui.theme.*

@Composable
fun TigerPetDialog(
    engine: GameEngine,
    onDismiss: () -> Unit
) {
    val tiger = engine.tiger
    var feedbackMsg by remember { mutableStateOf<String?>(null) }
    var currentPetState by remember { mutableStateOf(tiger.currentState) }

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
            border = androidx.compose.foundation.BorderStroke(2.dp, OrangeFlame)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Text(
                    text = "🐯 SUNDARBAN TIGER COMPANION",
                    color = OrangeFlame,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                // Tiger Avatar Box
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(MidnightBlueLight, CircleShape)
                        .border(3.dp, if (tiger.evolution == TigerEvolution.GOLDEN_TIGER) ElectricGold else OrangeFlame, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        when (tiger.evolution) {
                            TigerEvolution.ROYAL_BENGAL -> "🐅"
                            TigerEvolution.ARMORED_TIGER -> "🛡️"
                            TigerEvolution.GOLDEN_TIGER -> "👑"
                        },
                        fontSize = 44.sp
                    )
                }

                Text(
                    text = "${tiger.name} • ${tiger.evolution.name.replace('_', ' ')}",
                    color = ElectricGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Stats Grid
                Surface(
                    color = MidnightBlueLight,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Level:", color = TextSecondary, fontSize = 12.sp)
                            Text("Level ${tiger.level}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bonding Score:", color = TextSecondary, fontSize = 12.sp)
                            Text("${tiger.bondingScore}%", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Health (HP):", color = TextSecondary, fontSize = 12.sp)
                            Text("${tiger.health.toInt()}/${tiger.maxHealth.toInt()}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Attack Power:", color = TextSecondary, fontSize = 12.sp)
                            Text("${tiger.attackPower.toInt()}", color = CrimsonAlert, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Defense:", color = TextSecondary, fontSize = 12.sp)
                            Text("${tiger.defensePower.toInt()}", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Pet Combat Stance Commands
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(PetState.FOLLOW, PetState.DEFEND, PetState.ATTACK).forEach { state ->
                        val isSelected = currentPetState == state
                        OutlinedButton(
                            onClick = {
                                currentPetState = state
                                tiger.currentState = state
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) OrangeFlame else MidnightBlueLight
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricGold else TextSecondary),
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        ) {
                            Text(
                                state.name,
                                color = if (isSelected) ShadowBlack else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Action Buttons: Feed & Evolve
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val res = tiger.feed("Fresh Royal Meat")
                            feedbackMsg = res
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeFlame, contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("feed_tiger_btn")
                    ) {
                        Text("FEED TIGER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (tiger.evolve()) {
                                feedbackMsg = "Tiger Evolved to ${tiger.evolution.name}!"
                            } else {
                                feedbackMsg = "Reach Level 5 or 10 to evolve tiger!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("evolve_tiger_btn")
                    ) {
                        Text("EVOLVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                feedbackMsg?.let { msg ->
                    Text(
                        text = msg,
                        color = ElectricGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text("CLOSE", color = TextSecondary)
                }
            }
        }
    }
}
