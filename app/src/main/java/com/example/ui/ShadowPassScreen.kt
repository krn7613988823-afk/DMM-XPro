package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ShadowPassSystem
import com.example.ui.theme.*

@Composable
fun ShadowPassScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShadowBlack)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("pass_back_btn")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ElectricGold)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SHADOW PASS • SEASON 1", color = ElectricGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("NEO DHAKA REVOLUTION (50 TIERS)", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Surface(
                color = MidnightBlueLight,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
            ) {
                Text(
                    "TIER 6/50",
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pass Progress Bar
        Surface(
            color = MidnightBlueDark,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Season XP: 3,250 / 25,000", color = TextSecondary, fontSize = 12.sp)
                    Text("Gold Pass Active 👑", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 0.12f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = ElectricGold,
                    trackColor = MidnightBlueLight
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 50 Tiers List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ShadowPassSystem.TIERS) { tier ->
                Surface(
                    color = if (tier.isUnlocked) MidnightBlueLight else MidnightBlueDark,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (tier.isUnlocked) ElectricGold else Color.DarkGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = if (tier.isUnlocked) ElectricGold else Color.DarkGray,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "${tier.level}",
                                        color = ShadowBlack,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Column {
                                Text(
                                    "Free: ${tier.freeReward}",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Gold 👑: ${tier.goldReward}",
                                    color = ElectricGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (tier.isUnlocked) {
                            Icon(Icons.Default.Check, contentDescription = "Claimed", tint = EmeraldGreen)
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
