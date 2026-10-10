package com.example

import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class NinjaBattleRoyaleTest {

    @Test
    fun testAll21WeaponsRegistered() {
        assertEquals("Must register exactly 21 weapons", 21, WeaponRegistry.ALL_21_WEAPONS.size)

        // Verify specific required weapons from prompt
        val fist = WeaponRegistry.getById("fist")
        assertEquals(15f, fist.damage, 0.01f)
        assertEquals(2f, fist.rangeMeters, 0.01f)
        assertEquals(3, fist.comboCount)

        val goldKatana = WeaponRegistry.getById("gold_katana")
        assertEquals(50f, goldKatana.damage, 0.01f)
        assertEquals(3f, goldKatana.rangeMeters, 0.01f)
        assertEquals(0.5f, goldKatana.attackCooldownSeconds, 0.01f)

        val shadowKatana = WeaponRegistry.getById("shadow_katana")
        assertEquals(60f, shadowKatana.damage, 0.01f)

        val akBd = WeaponRegistry.getById("ak_bd")
        assertEquals(45f, akBd.damage, 0.01f)
        assertEquals(150f, akBd.rangeMeters, 0.01f)
        assertEquals(30, akBd.magazineSize)

        val awmGold = WeaponRegistry.getById("awm_gold")
        assertEquals(95f, awmGold.damage, 0.01f)
        assertEquals(400f, awmGold.rangeMeters, 0.01f)
        assertEquals(5, awmGold.magazineSize)

        val ultKatana = WeaponRegistry.getById("gold_katana_ultimate")
        assertEquals(75f, ultKatana.damage, 0.01f)
        assertEquals(4f, ultKatana.rangeMeters, 0.01f)

        val tigerClaw = WeaponRegistry.getById("tiger_claw")
        assertEquals(70f, tigerClaw.damage, 0.01f)

        val shadowCloak = WeaponRegistry.getById("shadow_cloak")
        assertEquals(30f, shadowCloak.attackCooldownSeconds, 0.01f)
    }

    @Test
    fun testCharacterDamageAndArmorAbsorption() {
        val ninja = CharacterEntity(
            id = "test_ninja",
            name = "TestShinobi",
            health = 200f,
            armor = 100f
        )

        // Taking 50 base damage: armor absorbs 60% (30 damage), remaining 20 hits health
        val dealt = ninja.takeDamage(50f, isHeadshot = false)
        assertEquals(20f, dealt, 0.01f)
        assertEquals(70f, ninja.armor, 0.01f)
        assertEquals(180f, ninja.health, 0.01f)

        // Headshot: doubles damage to 100, armor absorbs 60, remaining 40 hits health
        val headshotDmg = ninja.takeDamage(50f, isHeadshot = true)
        assertEquals(40f, headshotDmg, 0.01f)
        assertEquals(10f, ninja.armor, 0.01f) // 70 - 60 = 10
        assertEquals(140f, ninja.health, 0.01f) // 180 - 40 = 140
    }

    @Test
    fun testBlockParryMitigation() {
        val blockingNinja = CharacterEntity(
            id = "blocker",
            name = "Blocker",
            health = 200f,
            armor = 0f,
            stamina = 100f,
            isBlocking = true,
            equippedWeapon = WeaponRegistry.TONFA // +25% defense bonus -> 75% reduction
        )

        // 100 incoming damage -> 75% reduction -> 25 damage taken
        val actualDamage = blockingNinja.takeDamage(100f)
        assertEquals(25f, actualDamage, 0.01f)
        assertEquals(175f, blockingNinja.health, 0.01f)
        assertEquals(80f, blockingNinja.stamina, 0.01f)
    }

    @Test
    fun testTigerCompanionFeedingAndEvolution() {
        val tiger = TigerCompanion(level = 4, currentExp = 900, expToNextLevel = 1000)
        assertFalse("Cannot evolve before level 5", tiger.evolve())

        // Feed with royal meat -> grants +150 exp, triggering level up to 5
        tiger.feed("Royal Bengal Meat")
        assertEquals(5, tiger.level)

        // Now evolution to ARMORED_TIGER succeeds
        val evolved = tiger.evolve()
        assertTrue("Tiger should evolve at level 5", evolved)
        assertEquals(TigerEvolution.ARMORED_TIGER, tiger.evolution)
        assertEquals(190f, tiger.attackPower, 0.01f) // 120 + 20 (level) + 50 (evolve)
    }

    @Test
    fun testEconomyTransactions() {
        val economy = PlayerEconomy(takaBalance = 5000)

        // Can purchase 2500 Gold Katana
        assertTrue(economy.canAfford(2500))
        val bought = economy.purchase("gold_katana", "Gold Katana", 2500)
        assertTrue(bought)
        assertEquals(2500, economy.takaBalance)
        assertEquals(1, economy.transactionHistory.size)

        // Cannot afford 5000 AWM
        assertFalse(economy.canAfford(5000))
        assertFalse(economy.purchase("awm_gold", "AWM-GOLD", 5000))
        assertEquals(2500, economy.takaBalance)

        // Match victory reward: 500 loot + 3 kills (1500) + 2000 victory = 4000
        val earned = economy.awardMatchEarnings(lootTaka = 500, eliminations = 3, isVictory = true)
        assertEquals(4000, earned)
        assertEquals(6500, economy.takaBalance)
    }

    @Test
    fun testSafeZoneCalculations() {
        val safeZone = SafeZone(
            center = Vector3(0f, 0f, 0f),
            currentRadius = 100f
        )

        val insidePos = Vector3(30f, 0f, 40f) // distance = 50 < 100
        val outsidePos = Vector3(100f, 0f, 100f) // distance = 141.4 > 100

        assertTrue(insidePos.distance2D(safeZone.center) <= safeZone.currentRadius)
        assertFalse(outsidePos.distance2D(safeZone.center) <= safeZone.currentRadius)
    }

    @Test
    fun testShadowPassFiftyTiers() {
        assertEquals("Shadow Pass must contain 50 tiers", 50, ShadowPassSystem.TIERS.size)
        assertEquals(1, ShadowPassSystem.TIERS.first().level)
        assertEquals(50, ShadowPassSystem.TIERS.last().level)
        assertTrue("Tier 50 must have mythic reward", ShadowPassSystem.TIERS.last().goldReward.contains("MYTHIC"))
    }

    @Test
    fun testBanglaGkJutsuIntegrity() {
        val questions = BanglaGkBank.SAMPLE_QUESTIONS
        assertTrue("Question bank must have questions", questions.isNotEmpty())
        for (q in questions) {
            assertEquals("Each question must have 4 options", 4, q.options.size)
            assertTrue("Correct index must be in 0..3", q.correctIndex in 0..3)
            assertNotNull(q.trivia)
        }
    }
}
