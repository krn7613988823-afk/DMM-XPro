package com.example.model

enum class PetState {
    IDLE,
    FOLLOW,
    DEFEND,
    ATTACK
}

enum class TigerEvolution {
    ROYAL_BENGAL,    // Standard orange & black stripes, gold eyes
    ARMORED_TIGER,   // Steel plate harness and shadow claws
    GOLDEN_TIGER     // Radiance aura, electric gold fur & ultimate leap
}

data class TigerCompanion(
    val id: String = "tiger_01",
    var name: String = "Sundarban Sher",
    var level: Int = 5,
    var currentExp: Int = 450,
    var expToNextLevel: Int = 1000,
    var bondingScore: Int = 85,
    var health: Float = 1000f,
    val maxHealth: Float = 1000f,
    var attackPower: Float = 120f,
    var defensePower: Float = 80f,
    var currentState: PetState = PetState.FOLLOW,
    var evolution: TigerEvolution = TigerEvolution.ROYAL_BENGAL,
    var position: Vector3 = Vector3(-2f, 0f, -2f),
    var yawDegrees: Float = 0f,
    var targetEntityId: String? = null
) {
    fun feed(foodType: String): String {
        bondingScore = (bondingScore + 10).coerceAtMost(100)
        currentExp += 150
        health = (health + 200f).coerceAtMost(maxHealth)
        if (currentExp >= expToNextLevel) {
            level++
            currentExp -= expToNextLevel
            expToNextLevel += 500
            attackPower += 20f
            defensePower += 15f
            return "Tiger Leveled Up to Level $level!"
        }
        return "Tiger fed with $foodType! Bonding +10"
    }

    fun evolve(): Boolean {
        if (level >= 5 && evolution == TigerEvolution.ROYAL_BENGAL) {
            evolution = TigerEvolution.ARMORED_TIGER
            attackPower += 50f
            defensePower += 40f
            return true
        } else if (level >= 10 && evolution == TigerEvolution.ARMORED_TIGER) {
            evolution = TigerEvolution.GOLDEN_TIGER
            attackPower += 100f
            defensePower += 80f
            return true
        }
        return false
    }
}
