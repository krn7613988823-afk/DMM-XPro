package com.example.model

data class MapLocation(
    val id: String,
    val name: String,
    val bengaliName: String,
    val description: String,
    val center: Vector3,
    val radius: Float,
    val primaryLootTier: String,
    val landmarkStyle: String
)

data class Building3D(
    val id: String,
    val name: String,
    val position: Vector3,
    val width: Float,
    val height: Float,
    val depth: Float,
    val colorHex: Long,
    val hasRooftopAccess: Boolean = true,
    val stories: Int = 2,
    val buildingType: String = "HOUSE", // "CLOCK_TOWER", "FACTORY", "TIN_HOUSE", "HOUSE"
    val doorWidth: Float = 4.0f,
    var isDoorOpen: Boolean = false
)

data class GroundLootItem(
    val id: String,
    val name: String,
    val type: String, // "WEAPON", "MEDKIT", "VEST", "HELMET"
    val position: Vector3,
    val level: Int = 1,
    val weaponData: WeaponData? = null,
    var isPickedUp: Boolean = false
)

data class LootCrate(
    val id: String,
    val position: Vector3,
    val weapon: WeaponData,
    val ammoCount: Int,
    var isLooted: Boolean = false
)

data class JumpPad(
    val id: String,
    val position: Vector3,
    val launchForceY: Float = 18f,
    val launchForward: Float = 12f
)

data class SafeZone(
    var center: Vector3 = Vector3(0f, 0f, 0f),
    var currentRadius: Float = 300f,
    var targetRadius: Float = 300f,
    var shrinkRatePerSec: Float = 1.2f,
    var phase: Int = 1,
    var timerSeconds: Float = 120f,
    var damagePerSecond: Float = 5f
)

object NeoDhakaMap {
    // 20 Free Fire Bermuda & Neo Dhaka Map Locations
    val LOCATIONS = listOf(
        MapLocation("clock_tower", "Clock Tower (Sangsad)", "ক্লক টাওয়ার", "Iconic central clock tower palace with working clock face.", Vector3(0f, 0f, 40f), 120f, "LEGENDARY", "clock_tower"),
        MapLocation("peak", "Peak (High Mountain)", "পিক (পাহাড়)", "Highest elevation plateau, high loot density and intense combat.", Vector3(-70f, 15f, 80f), 100f, "HIGH", "mountain_peak"),
        MapLocation("factory", "Factory", "ফ্যাক্টরি", "Industrial steel complex with catwalks and container roofs.", Vector3(65f, 0f, 75f), 110f, "HIGH", "factory_steel"),
        MapLocation("bimasakti", "Bimasakti Strip", "বিমাশক্তি স্ট্রিপ", "Central airfield runway, sniper sightlines and watchtowers.", Vector3(0f, 0f, -70f), 130f, "SNIPER", "airfield_runway"),
        MapLocation("pochinok", "Pochinok Village", "পচিনক গ্রাম", "Cluster of 2-story and tin-roof village houses.", Vector3(-60f, 0f, -50f), 90f, "MEDIUM", "village_tin"),
        MapLocation("mill", "Mill", "মিল", "Timber sawmills, wooden ramps and lumber storage.", Vector3(80f, 0f, -60f), 85f, "MEDIUM", "wood_mill"),
        MapLocation("katulistiwa", "Katulistiwa", "কাতুলিস্তিওয়া", "Lakeside pavilions, bamboo bridge crossings and boats.", Vector3(-110f, 0f, 20f), 80f, "MEDIUM", "water_pavilions"),
        MapLocation("mars_electric", "Mars Electric", "মার্স ইলেকট্রিক", "High-voltage substation, transformer towers and electrical sheds.", Vector3(120f, 0f, 30f), 95f, "HIGH", "power_grid"),
        MapLocation("hangar", "Hangar", "হ্যাঙ্গার", "Large airplane maintenance hangars and military crates.", Vector3(40f, 0f, -120f), 100f, "HIGH", "hangar_bays"),
        MapLocation("graveyard", "Graveyard", "গ্রেভইয়ার্ড", "Historic stone tombs, mausoleums and misty stealth cover.", Vector3(-40f, 0f, -130f), 75f, "STEALTH", "stone_shrines"),
        MapLocation("cape_town", "Cape Town", "কেপ টাউন", "Coastal luxury mansions and palm avenues.", Vector3(150f, 0f, -90f), 90f, "HIGH", "coastal_villas"),
        MapLocation("rim_nam", "Rim Nam Village", "রিম নাম গ্রাম", "Stilt wooden huts on water canals.", Vector3(-150f, 0f, -70f), 80f, "MEDIUM", "stilt_huts"),
        MapLocation("shipyard", "Shipyard", "শিপইয়ার্ড", "Cargo container cranes, dry docks and rusty hulls.", Vector3(160f, 0f, 100f), 115f, "SNIPER", "cranes_docks"),
        MapLocation("sentosa", "Sentosa Island", "সেন্তোসা দ্বীপ", "Southern tropical resort connected by cable bridge.", Vector3(-120f, 0f, 160f), 105f, "HIGH", "resort_island"),
        MapLocation("kota_tua", "Kota Tua", "কোটা তুয়া", "Ancient colonial brick ruins and overgrown shrines.", Vector3(110f, 0f, 160f), 85f, "STEALTH", "brick_ruins"),
        MapLocation("riverside", "Riverside", "রিভারসাইড", "Riverfront fish market and wooden jetties.", Vector3(-80f, 0f, 210f), 80f, "MEDIUM", "river_docks"),
        MapLocation("plantation", "Plantation", "প্ল্যান্টেশন", "Tea garden pavilions and processing warehouses.", Vector3(70f, 0f, 220f), 90f, "MEDIUM", "tea_garden"),
        MapLocation("waterfront", "Waterfront", "ওয়াটারফ্রন্ট", "Beachside promenade, lighthouse and defense bunkers.", Vector3(0f, 0f, 250f), 100f, "HIGH", "lighthouse_beach"),
        MapLocation("mirpur_dojo", "Mirpur Dojo", "মিরপুর দোযো", "Traditional martial arts ninja academy and weapons cache.", Vector3(-200f, 0f, 0f), 95f, "HIGH_MELEE", "dojo_temple"),
        MapLocation("gulistan_bazaar", "Gulistan Bazaar", "গুলিস্তান মার্কেট", "Dense multi-story shopping complex and street market.", Vector3(200f, 0f, 0f), 95f, "CLOSE_COMBAT", "market_stalls")
    )

    // 15+ Enterable Buildings & Houses across Bermuda Map
    val TEST_ARENA_BUILDINGS = listOf(
        // Central Clock Tower (Sangsad Bhaban + Big Working Clock)
        Building3D("bldg_clock_tower", "Sangsad Clock Tower", Vector3(0f, 0f, 40f), 48f, 22f, 32f, 0xFFFFFFFF, stories = 3, buildingType = "CLOCK_TOWER"),

        // Houses in Front (2-Story and Tin Houses with Openable Doors)
        Building3D("house_pochinok_1", "Pochinok 2-Story House", Vector3(-18f, 0f, 18f), 14f, 8f, 12f, 0xFFFFF9C4, stories = 2, buildingType = "HOUSE", isDoorOpen = true),
        Building3D("house_pochinok_2", "Pochinok Tin Cottage", Vector3(18f, 0f, 18f), 12f, 6f, 10f, 0xFFCFD8DC, stories = 1, buildingType = "TIN_HOUSE", isDoorOpen = true),
        Building3D("house_pochinok_3", "Pochinok Brick Villa", Vector3(-28f, 0f, 32f), 16f, 9f, 14f, 0xFFFFE0B2, stories = 2, buildingType = "HOUSE", isDoorOpen = true),
        Building3D("house_pochinok_4", "Pochinok Storehouse", Vector3(28f, 0f, 32f), 14f, 7f, 12f, 0xFFD7CCC8, stories = 1, buildingType = "TIN_HOUSE", isDoorOpen = true),

        // Factory Industrial Buildings
        Building3D("bldg_factory_main", "Factory Steel Mill", Vector3(55f, 0f, 70f), 26f, 16f, 20f, 0xFF78909C, stories = 3, buildingType = "FACTORY"),
        Building3D("bldg_factory_shed", "Factory Storage Shed", Vector3(45f, 0f, 50f), 16f, 7f, 12f, 0xFF90A4AE, stories = 1, buildingType = "TIN_HOUSE", isDoorOpen = true),

        // Peak Fortress
        Building3D("bldg_peak_lodge", "Peak Mountain Outpost", Vector3(-60f, 0f, 70f), 22f, 12f, 18f, 0xFFBCAAA4, stories = 2, buildingType = "HOUSE", isDoorOpen = true),

        // Bimasakti Hangar & Control Tower
        Building3D("bldg_bimasakti_hangar", "Bimasakti Hangar Bay", Vector3(-25f, 0f, -40f), 24f, 10f, 18f, 0xFFB0BEC5, stories = 1, buildingType = "FACTORY", isDoorOpen = true),
        Building3D("bldg_bimasakti_tower", "Airfield Control Tower", Vector3(25f, 0f, -40f), 12f, 18f, 12f, 0xFFECEFF1, stories = 3, buildingType = "HOUSE"),

        // Additional Village & Market Houses
        Building3D("house_market_1", "Gulistan 3-Story Mart", Vector3(40f, 0f, -10f), 16f, 12f, 14f, 0xFFFFCC80, stories = 3, buildingType = "HOUSE", isDoorOpen = true),
        Building3D("house_market_2", "Old Town Brick House", Vector3(-40f, 0f, -10f), 16f, 8f, 14f, 0xFFE0E0E0, stories = 2, buildingType = "HOUSE", isDoorOpen = true),
        Building3D("house_tin_village_1", "Green Tin House", Vector3(-12f, 0f, -22f), 10f, 5f, 8f, 0xFFC8E6C9, stories = 1, buildingType = "TIN_HOUSE", isDoorOpen = true),
        Building3D("house_tin_village_2", "Blue Tin House", Vector3(12f, 0f, -22f), 10f, 5f, 8f, 0xFFBBDEFB, stories = 1, buildingType = "TIN_HOUSE", isDoorOpen = true)
    )

    // Floor Loot Items Scattered Inside & Around Houses (Free Fire Style: Weapons, Vests, Helmets, Medkits)
    val ARENA_GROUND_LOOT = listOf(
        GroundLootItem("loot_ak1", "AK-BD Assault Rifle", "WEAPON", Vector3(-18f, 0.2f, 18f), weaponData = WeaponRegistry.AK_BD),
        GroundLootItem("loot_vest3", "Vest Level 3 (Armor)", "VEST", Vector3(-16f, 0.2f, 20f), level = 3),
        GroundLootItem("loot_helmet3", "Helmet Level 3", "HELMET", Vector3(-20f, 0.2f, 19f), level = 3),
        GroundLootItem("loot_med1", "Medkit (+100 HP)", "MEDKIT", Vector3(-18f, 0.2f, 16f), level = 1),

        GroundLootItem("loot_awm", "AWM-GOLD Sniper", "WEAPON", Vector3(18f, 0.2f, 18f), weaponData = WeaponRegistry.AWM_GOLD),
        GroundLootItem("loot_katana", "Gold Katana Ultimate", "WEAPON", Vector3(0f, 0.2f, 10f), weaponData = WeaponRegistry.GOLD_KATANA_ULTIMATE),
        GroundLootItem("loot_vest4", "Vest Level 4 (Kevlar)", "VEST", Vector3(0f, 0.2f, 12f), level = 4),
        GroundLootItem("loot_med2", "Medkit (+100 HP)", "MEDKIT", Vector3(20f, 0.2f, 19f), level = 1),

        GroundLootItem("loot_m4", "M4-BD Rifle", "WEAPON", Vector3(-28f, 0.2f, 32f), weaponData = WeaponRegistry.M4_BD),
        GroundLootItem("loot_vest2", "Vest Level 2", "VEST", Vector3(28f, 0.2f, 32f), level = 2),
        GroundLootItem("loot_med3", "Medkit (+100 HP)", "MEDKIT", Vector3(-28f, 0.2f, 30f), level = 1),

        GroundLootItem("loot_shotgun", "Shotgun Desi", "WEAPON", Vector3(-12f, 0.2f, -22f), weaponData = WeaponRegistry.SHOTGUN_DESI),
        GroundLootItem("loot_shuriken", "Ninja Shuriken Stars", "WEAPON", Vector3(12f, 0.2f, -22f), weaponData = WeaponRegistry.SHURIKEN)
    )

    val ARENA_JUMP_PADS = listOf(
        JumpPad("pad_1", Vector3(8f, 0f, 12f), 18f, 10f),
        JumpPad("pad_2", Vector3(-8f, 0f, 12f), 18f, 10f),
        JumpPad("pad_3", Vector3(25f, 0f, 8f), 18f, 10f)
    )

    val ARENA_LOOT_CRATES = listOf(
        LootCrate("crate_1", Vector3(0f, 0f, 10f), WeaponRegistry.GOLD_KATANA_ULTIMATE, 30),
        LootCrate("crate_2", Vector3(-10f, 0f, 8f), WeaponRegistry.SHADOW_KATANA, 20),
        LootCrate("crate_3", Vector3(10f, 0f, 8f), WeaponRegistry.AWM_GOLD, 10)
    )
}
