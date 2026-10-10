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
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.model.SkinRegistry
import com.example.model.WeaponData
import com.example.model.WeaponRegistry
import com.example.ui.theme.*

@Composable
fun ShopScreen(
    engine: GameEngine,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Weapons, 1: Skins, 2: Pets
    var purchaseMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShadowBlack)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("shop_back_btn")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ElectricGold)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "GULISTAN BAZAAR SHOP",
                    color = ElectricGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "গুলিস্তান মার্কেট • WEAPONS & SKINS",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Taka balance
            Surface(
                color = MidnightBlueLight,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("৳", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "%,d".format(engine.economy.takaBalance),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MidnightBlueDark,
            contentColor = ElectricGold
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("WEAPONS (21)", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("SKINS (20+)", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("PET ITEMS", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Feedback notification
        purchaseMessage?.let { msg ->
            Surface(
                color = MidnightBlueLight,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(
                    text = msg,
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(WeaponRegistry.ALL_21_WEAPONS) { weapon ->
                        WeaponShopItemCard(
                            weapon = weapon,
                            canAfford = engine.economy.canAfford(weapon.priceTaka),
                            isEquipped = engine.player.equippedWeapon.id == weapon.id,
                            onBuy = {
                                if (engine.economy.purchase(weapon.id, weapon.name, weapon.priceTaka)) {
                                    engine.equipWeapon(weapon)
                                    purchaseMessage = "Purchased and equipped ${weapon.name}!"
                                    engine.audioManager.playKatanaSlash()
                                } else {
                                    purchaseMessage = "Not enough Taka! Play matches to earn more."
                                }
                            }
                        )
                    }
                }
            }
            1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(SkinRegistry.ALL_SKINS) { skin ->
                        Surface(
                            color = MidnightBlueDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (engine.player.activeSkin.id == skin.id) ElectricGold else Color.DarkGray),
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
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(skin.primaryColorHex), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(skin.accentColorHex), RoundedCornerShape(8.dp))
                                    )
                                    Column {
                                        Text(skin.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(skin.description, color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                if (engine.player.activeSkin.id == skin.id) {
                                    Text("EQUIPPED", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                } else {
                                    Button(
                                        onClick = {
                                            if (skin.unlocked || engine.economy.purchase(skin.id, skin.name, skin.priceTaka)) {
                                                engine.player.activeSkin = skin
                                                purchaseMessage = "Equipped ${skin.name}!"
                                            } else {
                                                purchaseMessage = "Not enough Taka for ${skin.name}!"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = ShadowBlack),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (skin.unlocked) "EQUIP" else "৳ ${skin.priceTaka}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = MidnightBlueDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🐯 Sundarban Tiger Provisions", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Feed and bond with your tiger companion to unlock armored and golden tiger forms.", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (engine.economy.purchase("pet_food_meat", "Tiger Feast", 300)) {
                                        val res = engine.tiger.feed("Fresh Royal Meat")
                                        purchaseMessage = res
                                    } else {
                                        purchaseMessage = "Need ৳ 300 Taka for Tiger Feast!"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeFlame, contentColor = TextPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("BUY TIGER FEAST (৳ 300) • +10 BONDING", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeaponShopItemCard(
    weapon: WeaponData,
    canAfford: Boolean,
    isEquipped: Boolean,
    onBuy: () -> Unit
) {
    Surface(
        color = MidnightBlueDark,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEquipped) ElectricGold else MidnightBlueLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(weapon.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Surface(
                        color = MidnightBlueLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            weapon.category.name,
                            color = CyanAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    "Dmg: ${weapon.damage.toInt()} • Range: ${weapon.rangeMeters.toInt()}m • Cooldown: ${weapon.attackCooldownSeconds}s",
                    color = TextGold,
                    fontSize = 11.sp
                )
                Text(
                    weapon.description,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            if (isEquipped) {
                Text(
                    "EQUIPPED",
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (weapon.priceTaka == 0) EmeraldGreen else ElectricGold,
                        contentColor = ShadowBlack
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (weapon.priceTaka == 0) "FREE" else "৳ ${weapon.priceTaka}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
