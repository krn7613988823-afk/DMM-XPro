package com.example.engine

import com.example.model.*
import kotlin.math.*
import kotlin.random.Random

enum class MatchState {
    LOBBY,
    COUNTDOWN,
    PLAYING,
    VICTORY_BOOYAH,
    DEFEAT
}

data class KillFeedEntry(
    val id: String,
    val killer: String,
    val victim: String,
    val weaponName: String,
    val isHeadshot: Boolean,
    val timestampMs: Long = System.currentTimeMillis()
)

class GameEngine(
    val audioManager: AudioManager
) {
    var matchState: MatchState = MatchState.PLAYING
    var alivePlayersCount: Int = 100
    var playerKills: Int = 0
    var survivalTimerSec: Float = 0f
    var animTimeSec: Float = 0f

    val player: CharacterEntity = CharacterEntity(
        id = "player_01",
        name = "GoldNinja",
        position = Vector3(0f, 0f, 0f),
        equippedWeapon = WeaponRegistry.AK_BD,
        activeSkin = SkinRegistry.GOLD_NINJA,
        currentAmmoInMag = 20,
        medkitCount = 3,
        helmetLevel = 3,
        vestLevel = 3
    )

    val camera: Camera3D = Camera3D(
        targetPosition = player.position,
        yawDegrees = 0f,
        pitchDegrees = 7f,
        targetDistance = 4.8f,
        currentDistance = 4.8f,
        shoulderOffset = 0.45f,
        heightOffset = 1.6f
    )

    val tiger: TigerCompanion = TigerCompanion()

    val safeZone: SafeZone = SafeZone(
        center = Vector3(0f, 0f, 0f),
        currentRadius = 250f,
        targetRadius = 50f,
        shrinkRatePerSec = 0.7f,
        phase = 1,
        timerSeconds = 180f,
        damagePerSecond = 5f
    )

    val buildings: List<Building3D> = NeoDhakaMap.TEST_ARENA_BUILDINGS
    val lootCrates: MutableList<LootCrate> = NeoDhakaMap.ARENA_LOOT_CRATES.toMutableList()
    val groundLootItems: MutableList<GroundLootItem> = NeoDhakaMap.ARENA_GROUND_LOOT.toMutableList()
    val jumpPads: List<JumpPad> = NeoDhakaMap.ARENA_JUMP_PADS

    val bots: MutableList<CharacterEntity> = mutableListOf()
    val projectiles: MutableList<Projectile3D> = mutableListOf()
    val slashes: MutableList<SlashEffect3D> = mutableListOf()
    val floatingDamageTexts: MutableList<FloatingDamageText> = mutableListOf()
    val killFeed: MutableList<KillFeedEntry> = mutableListOf()

    val economy: PlayerEconomy = PlayerEconomy()

    private val botNames = listOf(
        "Mirpur_Kensei", "Gulistan_Tiger", "Dhanmondi_Ghost", "Curzon_Shinobi",
        "Uttara_Viper", "OldDhaka_Ronin", "Bhashani_Blade", "Shurjo_Ninja",
        "Rooftop_Reaper", "Moholla_Assassin", "Padma_Sniper", "Chittagong_Claw",
        "Bermuda_Ace", "Clock_Sniper", "Peak_Titan", "Factory_Ghost",
        "Pochinok_Shadow", "Mill_Striker", "Hangar_Demon", "Graveyard_Wraith"
    )

    init {
        initMatch()
    }

    fun initMatch() {
        matchState = MatchState.PLAYING
        alivePlayersCount = 100
        playerKills = 0
        survivalTimerSec = 0f
        player.health = player.maxHealth
        player.armor = player.maxArmor
        player.stamina = player.maxStamina
        player.energyPoints = 150f
        player.isEliminated = false
        player.position = Vector3(0f, 0f, 0f)
        player.velocity = Vector3(0f, 0f, 0f)
        player.currentAmmoInMag = 20
        player.medkitCount = 3
        player.helmetLevel = 3
        player.vestLevel = 3

        bots.clear()
        projectiles.clear()
        slashes.clear()
        floatingDamageTexts.clear()
        killFeed.clear()
        groundLootItems.clear()
        groundLootItems.addAll(NeoDhakaMap.ARENA_GROUND_LOOT)

        // Spawn 20 Active Real Bots across Bermuda locations and houses
        val spawnPoints = listOf(
            // 5 visible directly in front
            Vector3(-4.5f, 0f, 13f),
            Vector3(0f, 0f, 17f),
            Vector3(4.5f, 0f, 14f),
            Vector3(-8f, 0f, 21f),
            Vector3(7.5f, 0f, 23f),
            // Near Pochinok houses & village
            Vector3(-18f, 0f, 22f),
            Vector3(18f, 0f, 22f),
            Vector3(-28f, 0f, 36f),
            Vector3(28f, 0f, 36f),
            // Near Clock Tower
            Vector3(-8f, 0f, 38f),
            Vector3(8f, 0f, 38f),
            Vector3(0f, 0f, 48f),
            // Near Factory & Peak
            Vector3(50f, 0f, 65f),
            Vector3(-55f, 0f, 65f),
            // Near Bimasakti strip
            Vector3(-20f, 0f, -35f),
            Vector3(20f, 0f, -35f),
            Vector3(-10f, 0f, -20f),
            Vector3(10f, 0f, -20f),
            Vector3(35f, 0f, -8f),
            Vector3(-35f, 0f, -8f)
        )

        for (i in 0 until 20) {
            val name = botNames[i % botNames.size]
            val pos = spawnPoints[i]
            val weapon = if (i % 3 == 0) WeaponRegistry.AK_BD else if (i % 3 == 1) WeaponRegistry.SHADOW_KATANA else WeaponRegistry.M4_BD

            bots.add(
                CharacterEntity(
                    id = "bot_$i",
                    name = "BOT_${name}",
                    isBot = true,
                    position = pos,
                    equippedWeapon = weapon,
                    activeSkin = NinjaSkin(
                        id = "skin_bot_$i",
                        name = "Red Assassin",
                        description = "Red Tactical Ninja",
                        primaryColorHex = 0xFFD32F2F,
                        accentColorHex = 0xFFFF5252,
                        clothTexture = "red_tactical"
                    ),
                    health = 200f,
                    maxHealth = 200f,
                    armor = 50f,
                    maxArmor = 100f
                )
            )
        }
    }

    fun update(deltaTime: Float, moveInputX: Float, moveInputY: Float) {
        if (matchState != MatchState.PLAYING) return

        survivalTimerSec += deltaTime
        animTimeSec += deltaTime

        updatePlayerPhysics(deltaTime, moveInputX, moveInputY)
        camera.update(player.position + Vector3(0f, 1.4f, 0f), buildings, deltaTime)
        updateTigerCompanion(deltaTime)
        updateBots(deltaTime)
        updateProjectiles(deltaTime)
        updateEffects(deltaTime)
        updateSafeZone(deltaTime)
        simulateBackgroundMatchEliminations(deltaTime)

        // Check Victory / Defeat
        if (player.isEliminated) {
            matchState = MatchState.DEFEAT
            audioManager.playHitImpact()
        } else if (alivePlayersCount <= 1 && bots.all { it.isEliminated }) {
            matchState = MatchState.VICTORY_BOOYAH
            audioManager.playVictoryBooyah()
            economy.awardMatchEarnings(500, playerKills, isVictory = true)
        }
    }

    private fun updatePlayerPhysics(dt: Float, inputX: Float, inputY: Float) {
        val speed = when {
            player.isShadowCloaked -> 12f
            player.isSprinting -> 9.5f
            player.isCrouching -> 3.5f
            else -> 6.0f
        }

        val camFwd = camera.getForward()
        val camRight = camera.getRight()
        val moveDir = (camRight * inputX + camFwd * inputY).normalized()

        if (moveDir.length() > 0.01f) {
            player.velocity.x = moveDir.x * speed
            player.velocity.z = moveDir.z * speed
            player.yawDegrees = Math.toDegrees(atan2(moveDir.x.toDouble(), moveDir.z.toDouble())).toFloat()
            if (player.isGrounded) {
                player.animState = if (player.isSprinting) CharacterAnimationState.SPRINT else CharacterAnimationState.WALK
            }
        } else {
            player.velocity.x *= 0.6f
            player.velocity.z *= 0.6f
            if (player.isGrounded && player.animState != CharacterAnimationState.SLASH && player.animState != CharacterAnimationState.SHOOT && player.animState != CharacterAnimationState.BLOCK) {
                player.animState = CharacterAnimationState.IDLE
            }
        }

        if (!player.isGrounded) {
            player.velocity.y -= 26f * dt
            player.animState = if (player.velocity.y > 0) CharacterAnimationState.JUMP else CharacterAnimationState.FALL
        }

        val nextPos = player.position + player.velocity * dt
        var currentGroundY = 0f

        for (b in buildings) {
            val minX = b.position.x - b.width / 2f
            val maxX = b.position.x + b.width / 2f
            val minZ = b.position.z - b.depth / 2f
            val maxZ = b.position.z + b.depth / 2f
            val roofY = b.position.y + b.height

            // Doorway open detection
            if (player.position.distance2D(b.position) < b.width / 2f + 4f) {
                b.isDoorOpen = true
            }

            // Rooftop check
            if (nextPos.x in (minX - 0.5f)..(maxX + 0.5f) && nextPos.z in (minZ - 0.5f)..(maxZ + 0.5f)) {
                if (player.position.y >= roofY - 0.4f) {
                    currentGroundY = max(currentGroundY, roofY)
                }
            }
        }

        if (nextPos.y <= currentGroundY) {
            nextPos.y = currentGroundY
            player.velocity.y = 0f
            player.isGrounded = true
        } else {
            player.isGrounded = false
        }

        player.position = nextPos

        // Check Ground Loot item pickup (Free Fire style)
        for (item in groundLootItems) {
            if (!item.isPickedUp && player.position.distanceTo(item.position) < 2.5f) {
                when (item.type) {
                    "WEAPON" -> item.weaponData?.let { equipWeapon(it) }
                    "MEDKIT" -> {
                        player.medkitCount++
                        player.heal(50f)
                    }
                    "VEST" -> {
                        player.vestLevel = item.level
                        player.restoreArmor(100f)
                    }
                    "HELMET" -> {
                        player.helmetLevel = item.level
                    }
                }
                item.isPickedUp = true
                audioManager.playKatanaSlash()
            }
        }

        // Jump Pads
        for (pad in jumpPads) {
            if (player.position.distance2D(pad.position) < 2.5f && player.position.y <= pad.position.y + 1f) {
                player.velocity.y = pad.launchForceY
                val fwd = player.velocity.normalized()
                player.velocity.x = fwd.x * pad.launchForward
                player.velocity.z = fwd.z * pad.launchForward
                player.isGrounded = false
                audioManager.playJump()
            }
        }

        // Loot Crates
        for (crate in lootCrates) {
            if (!crate.isLooted && player.position.distanceTo(crate.position) < 3.2f) {
                equipWeapon(crate.weapon)
                crate.isLooted = true
                audioManager.playKatanaSlash()
            }
        }

        // Reload Timer update
        if (player.isReloading) {
            player.reloadTimer -= dt
            if (player.reloadTimer <= 0f) {
                player.isReloading = false
                player.currentAmmoInMag = 20
            }
        }

        // Stamina & EP Regen
        player.stamina = (player.stamina + 15f * dt).coerceAtMost(player.maxStamina)
        player.energyPoints = (player.energyPoints + 5f * dt).coerceAtMost(player.maxEnergyPoints)

        if (player.isShadowCloaked) {
            player.cloakRemainingSeconds -= dt
            if (player.cloakRemainingSeconds <= 0f) {
                player.isShadowCloaked = false
            }
        }

        if (player.attackCooldown > 0f) {
            player.attackCooldown -= dt
        }
    }

    private fun updateTigerCompanion(dt: Float) {
        val targetPos = player.position - camera.getForward() * 2.5f + camera.getRight() * 1.6f
        val distToPlayer = tiger.position.distanceTo(player.position)

        if (distToPlayer > 25f) {
            tiger.position = targetPos
        } else if (distToPlayer > 3.2f) {
            val dir = (targetPos - tiger.position).normalized()
            tiger.position = tiger.position + dir * 7.5f * dt
            tiger.currentState = PetState.FOLLOW
        } else {
            tiger.currentState = PetState.IDLE
        }

        val closestBot = bots.filter { !it.isEliminated }.minByOrNull { it.position.distanceTo(tiger.position) }
        if (closestBot != null && closestBot.position.distanceTo(tiger.position) < 7f) {
            tiger.currentState = PetState.ATTACK
            val dir = (closestBot.position - tiger.position).normalized()
            tiger.position = tiger.position + dir * 9f * dt
            if (closestBot.position.distanceTo(tiger.position) < 2f) {
                closestBot.takeDamage(tiger.attackPower * dt)
                if (closestBot.isEliminated) {
                    onBotEliminated(closestBot, "Tiger [Sundarban]", "Tiger Claws")
                }
            }
        }
    }

    private fun updateBots(dt: Float) {
        for (bot in bots) {
            if (bot.isEliminated) continue

            val distToPlayer = bot.position.distanceTo(player.position)
            if (distToPlayer < 35f && !player.isShadowCloaked) {
                val dir = (player.position - bot.position).normalized()
                bot.yawDegrees = Math.toDegrees(atan2(dir.x.toDouble(), dir.z.toDouble())).toFloat()

                if (distToPlayer > 8f) {
                    bot.position = bot.position + dir * 4.5f * dt
                    bot.animState = CharacterAnimationState.WALK
                } else {
                    bot.attackCooldown -= dt
                    if (bot.attackCooldown <= 0f) {
                        bot.attackCooldown = 1.0f
                        bot.animState = CharacterAnimationState.SHOOT

                        // Bot shoots at player
                        audioManager.playGunshot()
                        val hitDmg = 25f
                        player.takeDamage(hitDmg)
                        audioManager.playHitImpact()
                        floatingDamageTexts.add(
                            FloatingDamageText(
                                position = player.position + Vector3(0f, 1.8f, 0f),
                                damage = hitDmg,
                                isHeadshot = false
                            )
                        )
                    }
                }
            } else {
                // Free Fire Bot roam: wander slowly
                bot.animState = CharacterAnimationState.IDLE
            }
        }
    }

    private fun updateProjectiles(dt: Float) {
        val iter = projectiles.iterator()
        while (iter.hasNext()) {
            val proj = iter.next()
            proj.position = proj.position + proj.velocity * dt
            proj.lifetimeSeconds -= dt

            var hit = false
            for (bot in bots) {
                if (!bot.isEliminated && bot.id != proj.senderId) {
                    val dist = proj.position.distanceTo(bot.position + Vector3(0f, 1.3f, 0f))
                    if (dist < 1.6f) {
                        val isHead = proj.position.y > bot.position.y + 1.55f
                        // Real Free Fire Damage: 250 on Headshot, 45 on body
                        val rawDmg = if (isHead) 250f else 45f
                        bot.takeDamage(rawDmg, isHead)
                        audioManager.playHitImpact()

                        floatingDamageTexts.add(
                            FloatingDamageText(
                                position = bot.position + Vector3(0f, 2.1f, 0f),
                                damage = rawDmg,
                                isHeadshot = isHead
                            )
                        )

                        if (bot.isEliminated) {
                            playerKills++
                            onBotEliminated(bot, player.name, proj.weapon.name, isHead)
                        }
                        hit = true
                        break
                    }
                }
            }

            if (hit || proj.lifetimeSeconds <= 0f) {
                iter.remove()
            }
        }
    }

    private fun updateEffects(dt: Float) {
        slashes.removeAll {
            it.lifetimeSeconds -= dt
            it.lifetimeSeconds <= 0f
        }
        floatingDamageTexts.removeAll {
            it.lifetimeSeconds -= dt
            it.position.y += 1.2f * dt
            it.lifetimeSeconds <= 0f
        }
    }

    private fun updateSafeZone(dt: Float) {
        safeZone.timerSeconds -= dt
        if (safeZone.timerSeconds <= 0f) {
            safeZone.phase++
            safeZone.timerSeconds = 90f
            safeZone.targetRadius = max(20f, safeZone.currentRadius * 0.6f)
            safeZone.damagePerSecond += 3f
        }

        if (safeZone.currentRadius > safeZone.targetRadius) {
            safeZone.currentRadius -= safeZone.shrinkRatePerSec * dt
        }

        val playerDistFromCenter = player.position.distance2D(safeZone.center)
        if (playerDistFromCenter > safeZone.currentRadius) {
            player.takeDamage(safeZone.damagePerSecond * dt)
        }

        for (bot in bots) {
            if (!bot.isEliminated && bot.position.distance2D(safeZone.center) > safeZone.currentRadius) {
                bot.takeDamage(safeZone.damagePerSecond * dt)
                if (bot.isEliminated) {
                    onBotEliminated(bot, "Safe Zone", "Zone Damage")
                }
            }
        }
    }

    private fun simulateBackgroundMatchEliminations(dt: Float) {
        if (alivePlayersCount > bots.count { !it.isEliminated } + 1 && Random.nextFloat() < 0.12f * dt) {
            alivePlayersCount--
            val killer = botNames.random()
            val victim = botNames.random()
            val weapon = WeaponRegistry.ALL_21_WEAPONS.random().name
            killFeed.add(
                0,
                KillFeedEntry(
                    id = "bg_${System.currentTimeMillis()}",
                    killer = killer,
                    victim = victim,
                    weaponName = weapon,
                    isHeadshot = Random.nextBoolean()
                )
            )
            if (killFeed.size > 5) killFeed.removeLast()
        }
    }

    private fun onBotEliminated(bot: CharacterEntity, killer: String, weaponName: String, isHeadshot: Boolean = false) {
        alivePlayersCount = max(1, alivePlayersCount - 1)
        killFeed.add(
            0,
            KillFeedEntry(
                id = "kf_${System.currentTimeMillis()}",
                killer = killer,
                victim = bot.name,
                weaponName = weaponName,
                isHeadshot = isHeadshot
            )
        )
        if (killFeed.size > 5) killFeed.removeLast()
        economy.awardMatchEarnings(200, 1, false)

        // Drop Free Fire Death Loot Box where bot fell
        groundLootItems.add(
            GroundLootItem(
                id = "loot_drop_${System.currentTimeMillis()}",
                name = "Death Crate: ${bot.equippedWeapon.name}",
                type = "WEAPON",
                position = bot.position.copy(y = 0.2f),
                weaponData = bot.equippedWeapon
            )
        )
    }

    // Player Actions
    fun triggerAttack() {
        if (player.attackCooldown > 0f || player.isEliminated) return

        if (player.equippedWeapon.category == WeaponCategory.FIREARM || player.equippedWeapon.id.contains("ak")) {
            if (player.currentAmmoInMag <= 0) {
                triggerReload()
                return
            }
            player.currentAmmoInMag--
            player.attackCooldown = player.equippedWeapon.attackCooldownSeconds
            performFirearmAttack()
        } else {
            player.attackCooldown = player.equippedWeapon.attackCooldownSeconds
            performMeleeAttack()
        }
    }

    private fun performMeleeAttack() {
        player.animState = CharacterAnimationState.SLASH
        audioManager.playKatanaSlash()

        val fwd = camera.getForward()
        val slashOrigin = player.position + Vector3(0f, 1.2f, 0f) + fwd * 0.8f

        slashes.add(
            SlashEffect3D(
                origin = slashOrigin,
                direction = fwd,
                isGoldUltimate = player.equippedWeapon.id == "gold_katana_ultimate"
            )
        )

        for (bot in bots) {
            if (!bot.isEliminated) {
                val dist = player.position.distanceTo(bot.position)
                if (dist <= player.equippedWeapon.rangeMeters + 1.2f) {
                    val toBot = (bot.position - player.position).normalized()
                    if (fwd.dot(toBot) > 0.35f) {
                        val dmg = bot.takeDamage(player.equippedWeapon.damage)
                        audioManager.playHitImpact()
                        floatingDamageTexts.add(
                            FloatingDamageText(
                                position = bot.position + Vector3(0f, 1.8f, 0f),
                                damage = dmg,
                                isHeadshot = false
                            )
                        )
                        if (bot.isEliminated) {
                            playerKills++
                            onBotEliminated(bot, player.name, player.equippedWeapon.name)
                        }
                    }
                }
            }
        }
    }

    private fun performFirearmAttack() {
        player.animState = CharacterAnimationState.SHOOT
        audioManager.playGunshot()

        val fwd = camera.getForward()
        val startPos = player.position + Vector3(0f, 1.4f, 0f) + fwd * 0.6f
        projectiles.add(
            Projectile3D(
                position = startPos,
                velocity = fwd * 75f,
                weapon = player.equippedWeapon,
                senderId = player.id
            )
        )
    }

    fun triggerReload() {
        if (player.isReloading || player.isEliminated) return
        player.isReloading = true
        player.reloadTimer = 1.8f
        audioManager.playKatanaSlash()
    }

    fun toggleScope4x() {
        player.isScoping4x = !player.isScoping4x
        camera.isAimingDownSights = player.isScoping4x
    }

    fun useMedkit() {
        if (player.medkitCount > 0 && player.health < player.maxHealth) {
            player.medkitCount--
            player.heal(100f)
            audioManager.playVictoryBooyah()
        }
    }

    fun triggerJump() {
        if (player.isGrounded && !player.isEliminated) {
            player.velocity.y = 13.5f
            player.isGrounded = false
            player.animState = CharacterAnimationState.JUMP
            audioManager.playJump()
        }
    }

    fun triggerShadowDash() {
        if (player.energyPoints >= 25f && !player.isEliminated) {
            player.energyPoints -= 25f
            val fwd = camera.getForward()
            player.position = player.position + fwd * 8.0f
            audioManager.playKatanaSlash()
        }
    }

    fun toggleCrouch() {
        player.isCrouching = !player.isCrouching
    }

    fun toggleBlock(blocking: Boolean) {
        player.isBlocking = blocking
        player.animState = if (blocking) CharacterAnimationState.BLOCK else CharacterAnimationState.IDLE
    }

    fun equipWeapon(weapon: WeaponData) {
        player.equippedWeapon = weapon
    }
}
