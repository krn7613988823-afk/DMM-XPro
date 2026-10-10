package com.example.model

enum class CharacterAnimationState {
    IDLE,
    WALK,
    SPRINT,
    JUMP,
    FALL,
    LAND,
    CROUCH,
    SLIDE,
    CLIMB,
    PUNCH,
    SLASH,
    BLOCK,
    RELOAD,
    SHOOT,
    HIT,
    KNOCKOUT
}

data class NinjaSkin(
    val id: String,
    val name: String,
    val description: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val clothTexture: String,
    val priceTaka: Int = 0,
    val unlocked: Boolean = false
)

object SkinRegistry {
    val SHADOW_DEFAULT = NinjaSkin(
        id = "skin_shadow",
        name = "Shadow Ninja",
        description = "Standard black tactical attire with electric gold eye glow.",
        primaryColorHex = 0xFF0D1B2A,
        accentColorHex = 0xFFFFD700,
        clothTexture = "tactical_dark",
        unlocked = true
    )

    val GOLD_NINJA = NinjaSkin(
        id = "skin_gold",
        name = "Gold Ninja",
        description = "Master ninja clad in polished Dhaka gold armor plating.",
        primaryColorHex = 0xFFFFD700,
        accentColorHex = 0xFFFFF176,
        clothTexture = "gold_plate",
        priceTaka = 5000
    )

    val LUNGI_NINJA = NinjaSkin(
        id = "skin_lungi",
        name = "Lungi Ninja",
        description = "Authentic checked blue/green lungi folded high for swift parkour.",
        primaryColorHex = 0xFF1976D2,
        accentColorHex = 0xFFFFD700,
        clothTexture = "lungi_check",
        priceTaka = 2000
    )

    val GAMCHA_NINJA = NinjaSkin(
        id = "skin_gamcha",
        name = "Gamcha Ninja",
        description = "Crimson and white woven gamcha wrapped as tactical mask and headband.",
        primaryColorHex = 0xFFD32F2F,
        accentColorHex = 0xFFFFFFFF,
        clothTexture = "gamcha_weave",
        priceTaka = 2500
    )

    val TIGER_NINJA = NinjaSkin(
        id = "skin_tiger",
        name = "Tiger Ninja",
        description = "Sundarban Royal Bengal Tiger pelt markings and fierce gold eyes.",
        primaryColorHex = 0xFFE65100,
        accentColorHex = 0xFFFFD700,
        clothTexture = "tiger_fur",
        priceTaka = 6000
    )

    val BD_JERSEY_NINJA = NinjaSkin(
        id = "skin_bd_jersey",
        name = "Bangladesh Jersey Ninja",
        description = "National emerald green jersey with bold crimson sun emblem.",
        primaryColorHex = 0xFF006A4E,
        accentColorHex = 0xFFF42A41,
        clothTexture = "jersey_green",
        priceTaka = 3000
    )

    val ALL_SKINS: List<NinjaSkin> = listOf(
        SHADOW_DEFAULT,
        GOLD_NINJA,
        LUNGI_NINJA,
        GAMCHA_NINJA,
        TIGER_NINJA,
        BD_JERSEY_NINJA
    )
}

data class CharacterEntity(
    val id: String,
    val name: String,
    val isBot: Boolean = false,
    var position: Vector3 = Vector3(0f, 0f, 0f),
    var velocity: Vector3 = Vector3(0f, 0f, 0f),
    var yawDegrees: Float = 0f,
    var pitchDegrees: Float = 0f,
    var health: Float = 200f,
    val maxHealth: Float = 200f,
    var stamina: Float = 100f,
    val maxStamina: Float = 100f,
    var armor: Float = 100f,
    val maxArmor: Float = 100f,
    var energyPoints: Float = 150f, // EP for Jutsu and special dashes
    val maxEnergyPoints: Float = 200f,
    var animState: CharacterAnimationState = CharacterAnimationState.IDLE,
    var equippedWeapon: WeaponData = WeaponRegistry.GOLD_KATANA,
    var activeSkin: NinjaSkin = SkinRegistry.SHADOW_DEFAULT,
    var isGrounded: Boolean = true,
    var isCrouching: Boolean = false,
    var isBlocking: Boolean = false,
    var isSprinting: Boolean = false,
    var isSliding: Boolean = false,
    var isShadowCloaked: Boolean = false,
    var cloakRemainingSeconds: Float = 0f,
    var attackCooldown: Float = 0f,
    var comboStep: Int = 0,
    var kills: Int = 0,
    var damageDealt: Float = 0f,
    var isEliminated: Boolean = false,
    var nameplateColor: Long = 0xFFFFD700,
    var helmetLevel: Int = 3,
    var vestLevel: Int = 3,
    var isReloading: Boolean = false,
    var reloadTimer: Float = 0f,
    var isScoping4x: Boolean = false,
    var muzzleFlashTimer: Float = 0f,
    var currentAmmoInMag: Int = 30,
    var medkitCount: Int = 3
) {
    fun takeDamage(amount: Float, isHeadshot: Boolean = false): Float {
        if (isEliminated) return 0f
        var effectiveDamage = amount
        if (isHeadshot) {
            effectiveDamage *= 2.0f
        }
        if (isBlocking) {
            val reduction = 0.5f + equippedWeapon.defenseBonus
            effectiveDamage *= (1.0f - reduction)
            stamina = (stamina - 20f).coerceAtLeast(0f)
        }

        // Armor absorbs 60% of damage first
        if (armor > 0f) {
            val armorDmg = effectiveDamage * 0.6f
            if (armor >= armorDmg) {
                armor -= armorDmg
                effectiveDamage -= armorDmg
            } else {
                effectiveDamage -= armor
                armor = 0f
            }
        }

        health = (health - effectiveDamage).coerceAtLeast(0f)
        if (health <= 0f) {
            isEliminated = true
            animState = CharacterAnimationState.KNOCKOUT
        } else {
            animState = CharacterAnimationState.HIT
        }
        return effectiveDamage
    }

    fun heal(amount: Float) {
        if (isEliminated) return
        health = (health + amount).coerceAtMost(maxHealth)
    }

    fun restoreArmor(amount: Float) {
        if (isEliminated) return
        armor = (armor + amount).coerceAtMost(maxArmor)
    }
}
