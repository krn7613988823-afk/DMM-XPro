package com.example.model

enum class WeaponCategory {
    MELEE,
    THROWABLE,
    FIREARM,
    SPECIAL
}

enum class AmmoType {
    NONE,
    KUNAI_DARTS,
    SHURIKEN_STARS,
    RIFLE_762,
    RIFLE_556,
    SNIPER_MAGNUM,
    SMG_9MM,
    SHOTGUN_SHELLS,
    PISTOL_9MM
}

data class WeaponData(
    val id: String,
    val name: String,
    val category: WeaponCategory,
    val damage: Float,
    val rangeMeters: Float,
    val attackCooldownSeconds: Float,
    val magazineSize: Int = 0,
    val currentAmmo: Int = 0,
    val reserveAmmo: Int = 0,
    val reloadTimeSeconds: Float = 0f,
    val fireRatePerSecond: Float = 1f,
    val comboCount: Int = 1,
    val defenseBonus: Float = 0f,
    val specialEffect: String = "",
    val priceTaka: Int = 0,
    val description: String = ""
)

object WeaponRegistry {
    // 1-8 MELEE
    val FIST = WeaponData(
        id = "fist",
        name = "Fist",
        category = WeaponCategory.MELEE,
        damage = 15f,
        rangeMeters = 2f,
        attackCooldownSeconds = 0.35f,
        comboCount = 3,
        description = "Basic martial arts strike with 3-hit combo."
    )

    val GOLD_KATANA = WeaponData(
        id = "gold_katana",
        name = "Gold Katana",
        category = WeaponCategory.MELEE,
        damage = 50f,
        rangeMeters = 3f,
        attackCooldownSeconds = 0.5f,
        comboCount = 3,
        priceTaka = 2500,
        description = "Forged in Mirpur with golden edges. Fast 0.5s cooldown."
    )

    val SHADOW_KATANA = WeaponData(
        id = "shadow_katana",
        name = "Shadow Katana",
        category = WeaponCategory.MELEE,
        damage = 60f,
        rangeMeters = 3f,
        attackCooldownSeconds = 0.75f,
        comboCount = 2,
        specialEffect = "Critical Strike Chance 35%",
        priceTaka = 3200,
        description = "Dark blade imbued with shadow steel. Higher critical hits."
    )

    val NUNCHAKU = WeaponData(
        id = "nunchaku",
        name = "Nunchaku",
        category = WeaponCategory.MELEE,
        damage = 30f,
        rangeMeters = 2.5f,
        attackCooldownSeconds = 0.25f,
        comboCount = 5,
        priceTaka = 1800,
        description = "Rapid spinning bludgeon with fast 5-hit combo."
    )

    val BAMBOO_LATHI = WeaponData(
        id = "bamboo_lathi",
        name = "Bamboo Lathi",
        category = WeaponCategory.MELEE,
        damage = 25f,
        rangeMeters = 3.5f,
        attackCooldownSeconds = 0.45f,
        comboCount = 3,
        priceTaka = 800,
        description = "Traditional Bangladeshi hardened bamboo quarterstaff. Long sweep."
    )

    val RICKSHAW_HANDLE = WeaponData(
        id = "rickshaw_handle",
        name = "Rickshaw Handle",
        category = WeaponCategory.MELEE,
        damage = 20f,
        rangeMeters = 2f,
        attackCooldownSeconds = 0.4f,
        comboCount = 3,
        priceTaka = 500,
        description = "Scavenged Old Dhaka rickshaw chrome handle. Sturdy blunt strike."
    )

    val TONFA = WeaponData(
        id = "tonfa",
        name = "Tonfa",
        category = WeaponCategory.MELEE,
        damage = 35f,
        rangeMeters = 2.5f,
        attackCooldownSeconds = 0.4f,
        comboCount = 3,
        defenseBonus = 0.25f,
        priceTaka = 1500,
        description = "Police/Ninja batons granting +25% block defense."
    )

    val DUAL_SAI = WeaponData(
        id = "dual_sai",
        name = "Dual Sai",
        category = WeaponCategory.MELEE,
        damage = 40f,
        rangeMeters = 2f,
        attackCooldownSeconds = 0.38f,
        comboCount = 4,
        priceTaka = 2200,
        description = "Twin pointed daggers for close quarter stabbing combinations."
    )

    // 9-12 THROWABLES
    val KUNAI = WeaponData(
        id = "kunai",
        name = "Kunai",
        category = WeaponCategory.THROWABLE,
        damage = 35f,
        rangeMeters = 15f,
        attackCooldownSeconds = 0.6f,
        magazineSize = 10,
        currentAmmo = 10,
        reserveAmmo = 20,
        priceTaka = 1200,
        description = "Aerodynamic throwing daggers. Direct piercing trajectory."
    )

    val SHURIKEN = WeaponData(
        id = "shuriken",
        name = "Shuriken",
        category = WeaponCategory.THROWABLE,
        damage = 25f,
        rangeMeters = 12f,
        attackCooldownSeconds = 0.5f,
        magazineSize = 15,
        currentAmmo = 15,
        reserveAmmo = 30,
        specialEffect = "3-Projectile Fan Spread",
        priceTaka = 1400,
        description = "Spinning star projectiles launched in a 3-way horizontal spread."
    )

    val SMOKE_BOMB = WeaponData(
        id = "smoke_bomb",
        name = "Smoke Bomb",
        category = WeaponCategory.THROWABLE,
        damage = 0f,
        rangeMeters = 10f,
        attackCooldownSeconds = 1.0f,
        magazineSize = 3,
        currentAmmo = 3,
        reserveAmmo = 5,
        specialEffect = "5m Smoke Screen for 5 seconds",
        priceTaka = 900,
        description = "Tactical smoke cloud for escape and repositioning."
    )

    val FLASH_KUNAI = WeaponData(
        id = "flash_kunai",
        name = "Flash Kunai",
        category = WeaponCategory.THROWABLE,
        damage = 10f,
        rangeMeters = 12f,
        attackCooldownSeconds = 1.0f,
        magazineSize = 3,
        currentAmmo = 3,
        reserveAmmo = 6,
        specialEffect = "Blinds targets for 2.5 seconds",
        priceTaka = 1100,
        description = "Explosive blinding flash kunai disorienting nearby opponents."
    )

    // 13-18 FIREARMS
    val AK_BD = WeaponData(
        id = "ak_bd",
        name = "AK-BD",
        category = WeaponCategory.FIREARM,
        damage = 45f,
        rangeMeters = 150f,
        attackCooldownSeconds = 0.33f,
        magazineSize = 30,
        currentAmmo = 30,
        reserveAmmo = 90,
        reloadTimeSeconds = 2.5f,
        fireRatePerSecond = 3f,
        priceTaka = 3500,
        description = "Heavy assault rifle modified for Neo Dhaka security forces."
    )

    val M4_BD = WeaponData(
        id = "m4_bd",
        name = "M4-BD",
        category = WeaponCategory.FIREARM,
        damage = 42f,
        rangeMeters = 180f,
        attackCooldownSeconds = 0.25f,
        magazineSize = 30,
        currentAmmo = 30,
        reserveAmmo = 90,
        reloadTimeSeconds = 2.2f,
        fireRatePerSecond = 4f,
        priceTaka = 3800,
        description = "Accurate burst rifle with high muzzle velocity."
    )

    val AWM_GOLD = WeaponData(
        id = "awm_gold",
        name = "AWM-GOLD",
        category = WeaponCategory.FIREARM,
        damage = 95f,
        rangeMeters = 400f,
        attackCooldownSeconds = 1.8f,
        magazineSize = 5,
        currentAmmo = 5,
        reserveAmmo = 20,
        reloadTimeSeconds = 3.2f,
        fireRatePerSecond = 0.55f,
        specialEffect = "One-Shot Headshot Multiplier 2.5x",
        priceTaka = 5000,
        description = "Gold-gilded bolt-action sniper rifle for extreme range picks."
    )

    val MP5_BD = WeaponData(
        id = "mp5_bd",
        name = "MP5-BD",
        category = WeaponCategory.FIREARM,
        damage = 30f,
        rangeMeters = 80f,
        attackCooldownSeconds = 0.16f,
        magazineSize = 30,
        currentAmmo = 30,
        reserveAmmo = 120,
        reloadTimeSeconds = 1.8f,
        fireRatePerSecond = 6f,
        priceTaka = 2800,
        description = "Rapid fire submachine gun tailored for alley warfare."
    )

    val SHOTGUN_DESI = WeaponData(
        id = "shotgun_desi",
        name = "Shotgun Desi",
        category = WeaponCategory.FIREARM,
        damage = 80f,
        rangeMeters = 10f,
        attackCooldownSeconds = 0.85f,
        magazineSize = 8,
        currentAmmo = 8,
        reserveAmmo = 32,
        reloadTimeSeconds = 2.8f,
        fireRatePerSecond = 1.2f,
        priceTaka = 2600,
        description = "Devastating close-range scatter buckshot."
    )

    val PISTOL_BD9 = WeaponData(
        id = "pistol_bd9",
        name = "Pistol BD-9",
        category = WeaponCategory.FIREARM,
        damage = 25f,
        rangeMeters = 50f,
        attackCooldownSeconds = 0.28f,
        magazineSize = 15,
        currentAmmo = 15,
        reserveAmmo = 45,
        reloadTimeSeconds = 1.5f,
        fireRatePerSecond = 3.5f,
        priceTaka = 1000,
        description = "Reliable standard-issue sidearm."
    )

    // 19-21 RARE WEAPONS & GADGETS
    val GOLD_KATANA_ULTIMATE = WeaponData(
        id = "gold_katana_ultimate",
        name = "Gold Katana Ultimate",
        category = WeaponCategory.SPECIAL,
        damage = 75f,
        rangeMeters = 4f,
        attackCooldownSeconds = 0.45f,
        comboCount = 4,
        specialEffect = "Gold Arc Blade Wave + Finisher",
        priceTaka = 8000,
        description = "Legendary sword of the Dhaka Ninja Clan. Unleashes radiant gold waves."
    )

    val TIGER_CLAW = WeaponData(
        id = "tiger_claw",
        name = "Tiger Claw",
        category = WeaponCategory.SPECIAL,
        damage = 70f,
        rangeMeters = 2f,
        attackCooldownSeconds = 0.35f,
        comboCount = 4,
        specialEffect = "Bleed Damage 10/sec",
        priceTaka = 6500,
        description = "Grounded steel gauntlet claws honoring the Sundarban Royal Bengal Tiger."
    )

    val SHADOW_CLOAK = WeaponData(
        id = "shadow_cloak",
        name = "Shadow Cloak",
        category = WeaponCategory.SPECIAL,
        damage = 0f,
        rangeMeters = 0f,
        attackCooldownSeconds = 30f,
        specialEffect = "Invisibility for 5.0 seconds",
        priceTaka = 7000,
        description = "Veils the ninja in pure shadow, evading enemy targeting and detection."
    )

    val ALL_21_WEAPONS: List<WeaponData> = listOf(
        FIST,
        GOLD_KATANA,
        SHADOW_KATANA,
        NUNCHAKU,
        BAMBOO_LATHI,
        RICKSHAW_HANDLE,
        TONFA,
        DUAL_SAI,
        KUNAI,
        SHURIKEN,
        SMOKE_BOMB,
        FLASH_KUNAI,
        AK_BD,
        M4_BD,
        AWM_GOLD,
        MP5_BD,
        SHOTGUN_DESI,
        PISTOL_BD9,
        GOLD_KATANA_ULTIMATE,
        TIGER_CLAW,
        SHADOW_CLOAK
    )

    fun getById(id: String): WeaponData = ALL_21_WEAPONS.firstOrNull { it.id == id } ?: FIST
}
