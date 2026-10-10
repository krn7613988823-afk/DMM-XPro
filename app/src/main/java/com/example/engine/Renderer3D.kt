package com.example.engine

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.model.*
import kotlin.math.*

data class Projectile3D(
    var position: Vector3,
    val velocity: Vector3,
    val weapon: WeaponData,
    val senderId: String,
    var lifetimeSeconds: Float = 3f,
    val isHeadshot: Boolean = false
)

data class SlashEffect3D(
    val origin: Vector3,
    val direction: Vector3,
    var lifetimeSeconds: Float = 0.35f,
    val maxLifetime: Float = 0.35f,
    val isGoldUltimate: Boolean = false
)

data class FloatingDamageText(
    var position: Vector3,
    val damage: Float,
    val isHeadshot: Boolean,
    var lifetimeSeconds: Float = 1.2f
)

class Renderer3D {

    private fun projectToScreen(
        worldPos: Vector3,
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        fovDeg: Float = 60f
    ): ScreenPoint? {
        val relPos = worldPos - camera.position

        val yawRad = Math.toRadians(-camera.yawDegrees.toDouble()).toFloat()
        val pitchRad = Math.toRadians(-camera.pitchDegrees.toDouble()).toFloat()

        val cosY = cos(yawRad)
        val sinY = sin(yawRad)
        val x1 = relPos.x * cosY - relPos.z * sinY
        val z1 = relPos.x * sinY + relPos.z * cosY
        val y1 = relPos.y

        val cosP = cos(pitchRad)
        val sinP = sin(pitchRad)
        val y2 = y1 * cosP - z1 * sinP
        val z2 = y1 * sinP + z1 * cosP
        val x2 = x1

        if (z2 <= 0.15f) return null

        val fovRad = Math.toRadians(fovDeg.toDouble()).toFloat()
        val fovScale = (screenHeight / 2f) / tan(fovRad / 2f)

        val screenX = screenWidth / 2f + (x2 / z2) * fovScale
        val screenY = screenHeight / 2f - (y2 / z2) * fovScale

        return ScreenPoint(screenX, screenY, z2)
    }

    data class ScreenPoint(val x: Float, val y: Float, val depth: Float)

    fun renderScene(
        scope: DrawScope,
        camera: Camera3D,
        player: CharacterEntity,
        bots: List<CharacterEntity>,
        tiger: TigerCompanion?,
        safeZone: SafeZone,
        buildings: List<Building3D>,
        crates: List<LootCrate>,
        groundLoot: List<GroundLootItem>,
        jumpPads: List<JumpPad>,
        projectiles: List<Projectile3D>,
        slashes: List<SlashEffect3D>,
        floatingTexts: List<FloatingDamageText>,
        animTimeSec: Float
    ) {
        val w = scope.size.width
        val h = scope.size.height

        // 1. BRIGHT DAYLIGHT SKY (#87CEEB)
        scope.drawRect(
            color = Color(0xFF87CEEB),
            size = scope.size
        )

        // Horizon Warm Atmosphere
        scope.drawRect(
            color = Color(0xFFB0E0E6),
            topLeft = Offset(0f, h * 0.35f),
            size = androidx.compose.ui.geometry.Size(w, h * 0.25f)
        )

        // Bright Radiant Sun with Corona
        scope.drawCircle(
            color = Color(0x55FFD54F),
            radius = 70f,
            center = Offset(w * 0.85f, h * 0.15f)
        )
        scope.drawCircle(
            color = Color(0xFFFFF59D),
            radius = 45f,
            center = Offset(w * 0.85f, h * 0.15f)
        )

        // Clouds
        drawCloud(scope, Offset(w * 0.2f, h * 0.12f), 60f)
        drawCloud(scope, Offset(w * 0.55f, h * 0.08f), 75f)

        // 2. GREEN GRASS GROUND & ROADS
        drawBrightGroundAndRoads(scope, camera, w, h)

        // 3. 3D BUILDINGS & HOUSES WITH DOORS & CLOCK TOWER
        for (b in buildings) {
            drawBuilding(scope, b, camera, w, h, animTimeSec)
        }

        // 4. Ground Loot on floor (AK-BD, AWM, Vests, Helmets, Medkits)
        for (item in groundLoot) {
            if (!item.isPickedUp) {
                drawGroundLoot(scope, item, camera, w, h, animTimeSec)
            }
        }

        // 5. Jump Pads & Crates
        for (pad in jumpPads) {
            drawJumpPad(scope, pad, camera, w, h, animTimeSec)
        }
        for (crate in crates) {
            if (!crate.isLooted) {
                drawLootCrate(scope, crate, camera, w, h, animTimeSec)
            }
        }

        // 6. Safe Zone Barrier
        drawSafeZoneBarrier(scope, safeZone, camera, w, h, animTimeSec)

        // 7. Render 20 Enemy Bots (Red Hostiles with weapons & health bars)
        for (bot in bots) {
            if (!bot.isEliminated) {
                drawNinjaCharacter(scope, bot, camera, w, h, animTimeSec, isPlayer = false)
            }
        }

        // 8. Render Hero Ninja (Big, Centered, Gold Armor with Shine, Helmet, Vest, AK-47 / Katana)
        drawNinjaCharacter(scope, player, camera, w, h, animTimeSec, isPlayer = true)

        // 9. Tiger Companion
        if (tiger != null) {
            drawTigerCompanion(scope, tiger, camera, w, h, animTimeSec)
        }

        // 10. Slashes & Projectiles (Bullet Trails + Muzzle Flash)
        for (slash in slashes) {
            drawSlashEffect(scope, slash, camera, w, h)
        }
        for (proj in projectiles) {
            drawProjectile(scope, proj, camera, w, h)
        }

        // 11. Floating Damage Numbers & Headshots ("HEADSHOT 250")
        for (dmg in floatingTexts) {
            drawFloatingDamage(scope, dmg, camera, w, h)
        }
    }

    private fun drawCloud(scope: DrawScope, center: Offset, radius: Float) {
        val cloudColor = Color(0xD8FFFFFF)
        scope.drawCircle(cloudColor, radius, center)
        scope.drawCircle(cloudColor, radius * 0.75f, Offset(center.x - radius * 0.8f, center.y + radius * 0.1f))
        scope.drawCircle(cloudColor, radius * 0.85f, Offset(center.x + radius * 0.8f, center.y + radius * 0.1f))
    }

    private fun drawBrightGroundAndRoads(scope: DrawScope, camera: Camera3D, w: Float, h: Float) {
        val horizonPt = projectToScreen(Vector3(0f, 0f, 350f), camera, w, h)
        val horizonY = (horizonPt?.y ?: (h * 0.44f)).coerceIn(h * 0.2f, h * 0.65f)

        // Green Grass Field
        scope.drawRect(
            color = Color(0xFF4CAF50),
            topLeft = Offset(0f, horizonY),
            size = androidx.compose.ui.geometry.Size(w, h - horizonY)
        )

        // Main Bermuda Asphalt Road
        val r0 = projectToScreen(Vector3(-7f, 0f, -30f), camera, w, h)
        val r1 = projectToScreen(Vector3(7f, 0f, -30f), camera, w, h)
        val r2 = projectToScreen(Vector3(7f, 0f, 180f), camera, w, h)
        val r3 = projectToScreen(Vector3(-7f, 0f, 180f), camera, w, h)

        if (r0 != null && r1 != null && r2 != null && r3 != null) {
            val roadPath = Path().apply {
                moveTo(r0.x, r0.y)
                lineTo(r1.x, r1.y)
                lineTo(r2.x, r2.y)
                lineTo(r3.x, r3.y)
                close()
            }
            scope.drawPath(roadPath, color = Color(0xFF37474F), style = Fill)
            scope.drawLine(Color(0xFFFFD54F), Offset(r0.x, r0.y), Offset(r3.x, r3.y), strokeWidth = 3f)
            scope.drawLine(Color(0xFFFFD54F), Offset(r1.x, r1.y), Offset(r2.x, r2.y), strokeWidth = 3f)

            // Dashed White Center Line
            for (step in 0..14) {
                val zStart = -25f + step * 14f
                val zEnd = zStart + 7f
                val d1 = projectToScreen(Vector3(0f, 0.05f, zStart), camera, w, h)
                val d2 = projectToScreen(Vector3(0f, 0.05f, zEnd), camera, w, h)
                if (d1 != null && d2 != null) {
                    scope.drawLine(Color(0xFFFFFFFF), Offset(d1.x, d1.y), Offset(d2.x, d2.y), strokeWidth = 3.5f)
                }
            }
        }
    }

    private fun drawBuilding(scope: DrawScope, b: Building3D, camera: Camera3D, w: Float, h: Float, animTime: Float) {
        val hw = b.width / 2f
        val hd = b.depth / 2f
        val bx = b.position.x
        val by = b.position.y
        val bz = b.position.z
        val bh = b.height

        val c0 = projectToScreen(Vector3(bx - hw, by, bz - hd), camera, w, h)
        val c1 = projectToScreen(Vector3(bx + hw, by, bz - hd), camera, w, h)
        val c2 = projectToScreen(Vector3(bx + hw, by, bz + hd), camera, w, h)
        val c3 = projectToScreen(Vector3(bx - hw, by, bz + hd), camera, w, h)

        val t0 = projectToScreen(Vector3(bx - hw, by + bh, bz - hd), camera, w, h)
        val t1 = projectToScreen(Vector3(bx + hw, by + bh, bz - hd), camera, w, h)
        val t2 = projectToScreen(Vector3(bx + hw, by + bh, bz + hd), camera, w, h)
        val t3 = projectToScreen(Vector3(bx - hw, by + bh, bz + hd), camera, w, h)

        val isClockTower = b.buildingType == "CLOCK_TOWER"
        val wallColor = Color(b.colorHex)
        val outlineColor = Color(0xFF263238)

        // Front Face
        if (t0 != null && t1 != null && c1 != null && c0 != null) {
            val frontPath = Path().apply {
                moveTo(c0.x, c0.y)
                lineTo(t0.x, t0.y)
                lineTo(t1.x, t1.y)
                lineTo(c1.x, c1.y)
                close()
            }
            scope.drawPath(frontPath, color = wallColor, style = Fill)
            scope.drawPath(frontPath, color = outlineColor, style = Stroke(width = 2.5f))

            // OPEN DOORWAY (Player can walk right in!)
            val doorW = b.doorWidth
            val d0 = projectToScreen(Vector3(bx - doorW / 2f, by, bz - hd), camera, w, h)
            val d1 = projectToScreen(Vector3(bx + doorW / 2f, by, bz - hd), camera, w, h)
            val dTop0 = projectToScreen(Vector3(bx - doorW / 2f, by + 3.8f, bz - hd), camera, w, h)
            val dTop1 = projectToScreen(Vector3(bx + doorW / 2f, by + 3.8f, bz - hd), camera, w, h)

            if (d0 != null && d1 != null && dTop0 != null && dTop1 != null) {
                val doorPath = Path().apply {
                    moveTo(d0.x, d0.y)
                    lineTo(dTop0.x, dTop0.y)
                    lineTo(dTop1.x, dTop1.y)
                    lineTo(d1.x, d1.y)
                    close()
                }
                // Dark Interior entrance
                scope.drawPath(doorPath, color = Color(0xFF1E2833), style = Fill)
                scope.drawPath(doorPath, color = Color(0xFFFFD700), style = Stroke(width = 2f))

                // Door frame label
                val midX = (d0.x + d1.x) / 2f
                val paint = Paint().apply {
                    color = android.graphics.Color.YELLOW
                    textSize = 14f
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }
                scope.drawContext.canvas.nativeCanvas.drawText("🚪 OPEN DOOR", midX, dTop0.y - 4f, paint)
            }

            // CLOCK TOWER: BIG WORKING CLOCK FACE
            if (isClockTower) {
                val clockCenter = projectToScreen(Vector3(bx, by + bh * 0.72f, bz - hd), camera, w, h)
                if (clockCenter != null) {
                    val clockRadius = (32f * (45f / clockCenter.depth)).coerceIn(16f, 50f)
                    // Clock Dial
                    scope.drawCircle(Color(0xFFFFFFFF), clockRadius, Offset(clockCenter.x, clockCenter.y))
                    scope.drawCircle(Color(0xFFFFD700), clockRadius, Offset(clockCenter.x, clockCenter.y), style = Stroke(width = 3.5f))

                    // Clock Hands (Moving with time!)
                    val hourAngle = ((animTime * 0.1f) % (2f * Math.PI.toFloat()))
                    val minAngle = ((animTime * 0.6f) % (2f * Math.PI.toFloat()))
                    val hx = clockCenter.x + cos(hourAngle) * clockRadius * 0.5f
                    val hy = clockCenter.y + sin(hourAngle) * clockRadius * 0.5f
                    val mx = clockCenter.x + cos(minAngle) * clockRadius * 0.75f
                    val my = clockCenter.y + sin(minAngle) * clockRadius * 0.75f
                    scope.drawLine(
                        Color(0xFF212121),
                        Offset(clockCenter.x, clockCenter.y),
                        Offset(hx, hy),
                        strokeWidth = 3f
                    )
                    scope.drawLine(
                        Color(0xFFE53935),
                        Offset(clockCenter.x, clockCenter.y),
                        Offset(mx, my),
                        strokeWidth = 2f
                    )
                }
            }
        }

        // Side Face
        if (t1 != null && t2 != null && c2 != null && c1 != null) {
            val sidePath = Path().apply {
                moveTo(c1.x, c1.y)
                lineTo(t1.x, t1.y)
                lineTo(t2.x, t2.y)
                lineTo(c2.x, c2.y)
                close()
            }
            scope.drawPath(sidePath, color = wallColor.copy(alpha = 0.9f), style = Fill)
            scope.drawPath(sidePath, color = outlineColor, style = Stroke(width = 2.0f))
        }

        // Rooftop
        if (t0 != null && t1 != null && t2 != null && t3 != null) {
            val roofPath = Path().apply {
                moveTo(t0.x, t0.y)
                lineTo(t1.x, t1.y)
                lineTo(t2.x, t2.y)
                lineTo(t3.x, t3.y)
                close()
            }
            val roofColor = if (b.buildingType == "TIN_HOUSE") Color(0xFF78909C) else wallColor
            scope.drawPath(roofPath, color = roofColor, style = Fill)
            scope.drawPath(roofPath, color = Color(0xFFFFD700), style = Stroke(width = 2f))
        }

        // Building Label
        val topCenter = projectToScreen(Vector3(bx, by + bh + 1.2f, bz), camera, w, h)
        if (topCenter != null && topCenter.depth < 100f) {
            val paint = Paint().apply {
                color = if (isClockTower) android.graphics.Color.rgb(255, 215, 0) else android.graphics.Color.BLACK
                textSize = (16f * (40f / topCenter.depth)).coerceIn(16f, 30f)
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
            scope.drawContext.canvas.nativeCanvas.drawText("🏠 ${b.name}", topCenter.x, topCenter.y, paint)
        }
    }

    private fun drawGroundLoot(scope: DrawScope, item: GroundLootItem, camera: Camera3D, w: Float, h: Float, animTime: Float) {
        val pt = projectToScreen(item.position + Vector3(0f, 0.2f + 0.08f * sin(animTime * 4f), 0f), camera, w, h) ?: return
        if (pt.depth > 60f) return

        val scale = (35f / pt.depth).coerceIn(0.8f, 2.8f)

        when (item.type) {
            "WEAPON" -> {
                // Long Realistic Gun on ground
                scope.drawLine(
                    Color(0xFF212121),
                    Offset(pt.x - 14f * scale, pt.y),
                    Offset(pt.x + 14f * scale, pt.y - 2f * scale),
                    strokeWidth = 4.5f * scale
                )
                // Golden highlight
                scope.drawLine(
                    Color(0xFFFFD700),
                    Offset(pt.x - 6f * scale, pt.y),
                    Offset(pt.x + 12f * scale, pt.y - 2f * scale),
                    strokeWidth = 2.5f * scale
                )
            }
            "MEDKIT" -> {
                // White Medkit Box with Green Cross
                scope.drawRect(
                    Color(0xFFFFFFFF),
                    topLeft = Offset(pt.x - 8f * scale, pt.y - 6f * scale),
                    size = androidx.compose.ui.geometry.Size(16f * scale, 12f * scale)
                )
                scope.drawRect(
                    Color(0xFF2E7D32),
                    topLeft = Offset(pt.x - 8f * scale, pt.y - 6f * scale),
                    size = androidx.compose.ui.geometry.Size(16f * scale, 12f * scale),
                    style = Stroke(width = 1.5f)
                )
                // Cross
                scope.drawLine(Color(0xFF2E7D32), Offset(pt.x, pt.y - 4f * scale), Offset(pt.x, pt.y + 4f * scale), strokeWidth = 2f * scale)
                scope.drawLine(Color(0xFF2E7D32), Offset(pt.x - 4f * scale, pt.y), Offset(pt.x + 4f * scale, pt.y), strokeWidth = 2f * scale)
            }
            "VEST" -> {
                // Ballistic Vest on ground
                scope.drawRect(
                    Color(0xFF455A64),
                    topLeft = Offset(pt.x - 9f * scale, pt.y - 7f * scale),
                    size = androidx.compose.ui.geometry.Size(18f * scale, 14f * scale)
                )
                scope.drawRect(
                    Color(0xFF00E5FF),
                    topLeft = Offset(pt.x - 9f * scale, pt.y - 7f * scale),
                    size = androidx.compose.ui.geometry.Size(18f * scale, 14f * scale),
                    style = Stroke(width = 2f)
                )
            }
            "HELMET" -> {
                // Tactical Helmet
                scope.drawCircle(Color(0xFF37474F), 8f * scale, Offset(pt.x, pt.y))
                scope.drawCircle(Color(0xFFFFD700), 8f * scale, Offset(pt.x, pt.y), style = Stroke(width = 2f))
            }
        }

        // Floating Item Name & Level
        val paint = Paint().apply {
            color = when (item.type) {
                "WEAPON" -> android.graphics.Color.YELLOW
                "MEDKIT" -> android.graphics.Color.GREEN
                "VEST" -> android.graphics.Color.CYAN
                else -> android.graphics.Color.WHITE
            }
            textSize = (13f * 2.0f * scale).coerceIn(14f, 26f)
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        scope.drawContext.canvas.nativeCanvas.drawText("✦ ${item.name}", pt.x, pt.y - 12f * scale, paint)
    }

    private fun drawJumpPad(scope: DrawScope, pad: JumpPad, camera: Camera3D, w: Float, h: Float, animTime: Float) {
        val pt = projectToScreen(pad.position, camera, w, h) ?: return
        val pulse = 1f + 0.2f * sin(animTime * 6f)
        val radius = (35f / pt.depth.coerceAtLeast(1f) * 12f) * pulse

        scope.drawCircle(Color(0xFFFFD700).copy(alpha = 0.4f), radius * 1.3f, Offset(pt.x, pt.y))
        scope.drawCircle(Color(0xFFFFD700), radius, Offset(pt.x, pt.y), style = Stroke(width = 3.5f))
        scope.drawCircle(Color(0xFF00E5FF), radius * 0.45f, Offset(pt.x, pt.y))
    }

    private fun drawLootCrate(scope: DrawScope, crate: LootCrate, camera: Camera3D, w: Float, h: Float, animTime: Float) {
        val pt = projectToScreen(crate.position + Vector3(0f, 0.4f + 0.08f * sin(animTime * 3f), 0f), camera, w, h) ?: return
        val size = (40f / pt.depth.coerceAtLeast(1f) * 12f).coerceIn(16f, 65f)

        scope.drawRect(Color(0xFFFFD700), topLeft = Offset(pt.x - size / 2f, pt.y - size / 2f), size = androidx.compose.ui.geometry.Size(size, size))
        scope.drawRect(Color(0xFF212121), topLeft = Offset(pt.x - size / 2f, pt.y - size / 2f), size = androidx.compose.ui.geometry.Size(size, size), style = Stroke(width = 2.5f))

        if (pt.depth < 35f) {
            val paint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 20f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
            scope.drawContext.canvas.nativeCanvas.drawText("⚡ ${crate.weapon.name}", pt.x, pt.y - size * 0.7f, paint)
        }
    }

    private fun drawSafeZoneBarrier(scope: DrawScope, safeZone: SafeZone, camera: Camera3D, w: Float, h: Float, animTime: Float) {
        val segments = 28
        val points = mutableListOf<ScreenPoint>()
        for (i in 0 until segments) {
            val angle = (i.toFloat() / segments) * 2f * Math.PI.toFloat()
            val wx = safeZone.center.x + safeZone.currentRadius * cos(angle)
            val wz = safeZone.center.z + safeZone.currentRadius * sin(angle)
            val pt = projectToScreen(Vector3(wx, 0f, wz), camera, w, h)
            if (pt != null) {
                points.add(pt)
            }
        }

        for (i in 0 until points.size - 1) {
            scope.drawLine(Color(0xFF00E5FF).copy(alpha = 0.85f), Offset(points[i].x, points[i].y), Offset(points[i + 1].x, points[i + 1].y), strokeWidth = 4f)
        }
    }

    private fun drawNinjaCharacter(
        scope: DrawScope,
        char: CharacterEntity,
        camera: Camera3D,
        w: Float,
        h: Float,
        animTime: Float,
        isPlayer: Boolean
    ) {
        val basePos = char.position
        val headPt = projectToScreen(basePos + Vector3(0f, 2.0f, 0f), camera, w, h) ?: return
        val torsoPt = projectToScreen(basePos + Vector3(0f, 1.25f, 0f), camera, w, h) ?: return
        val feetPt = projectToScreen(basePos + Vector3(0f, 0.05f, 0f), camera, w, h) ?: return

        val depth = torsoPt.depth.coerceAtLeast(0.5f)
        val charHeightOnScreen = abs(feetPt.y - headPt.y)

        // Heroic Size (Occupies 70% of screen center for player)
        val scale = if (isPlayer) {
            (charHeightOnScreen / 65f).coerceIn(2.2f, 3.8f)
        } else {
            (charHeightOnScreen / 75f).coerceIn(0.9f, 2.8f)
        }

        // Gold Armor with Real Specular Shine Reflection for Player
        // Red Tactical Suit for Hostile Bots
        val armorColor = if (isPlayer) Color(0xFFFFD700) else Color(0xFFD32F2F)
        val specularShine = if (isPlayer) Color(0xFFFFFDE7) else Color(0xFFFF8A80)
        val outlineColor = Color(0xFF1A1A1A)

        val isMoving = char.velocity.length() > 0.5f
        val stride = if (isMoving) sin(animTime * if (char.isSprinting) 14f else 8f) else 0f
        val crouchOffset = if (char.isCrouching) 14f * scale else 0f

        // 1. Ground Shadow
        val shadowWidth = 26f * scale
        val shadowHeight = 9f * scale
        scope.drawOval(
            color = Color(0x66000000),
            topLeft = Offset(feetPt.x - shadowWidth / 2f, feetPt.y - shadowHeight / 2f),
            size = androidx.compose.ui.geometry.Size(shadowWidth, shadowHeight)
        )

        // 2. Legs & Boots (Realistic Striding Animation)
        val hipY = torsoPt.y + 14f * scale + crouchOffset
        val legLeftX = torsoPt.x - 7f * scale + stride * 6f * scale
        val legRightX = torsoPt.x + 7f * scale - stride * 6f * scale

        // Leg outlines & fills
        scope.drawLine(outlineColor, Offset(torsoPt.x - 5f * scale, hipY), Offset(legLeftX, feetPt.y), strokeWidth = 11f * scale)
        scope.drawLine(outlineColor, Offset(torsoPt.x + 5f * scale, hipY), Offset(legRightX, feetPt.y), strokeWidth = 11f * scale)

        scope.drawLine(armorColor, Offset(torsoPt.x - 5f * scale, hipY), Offset(legLeftX, feetPt.y), strokeWidth = 9f * scale)
        scope.drawLine(armorColor, Offset(torsoPt.x + 5f * scale, hipY), Offset(legRightX, feetPt.y), strokeWidth = 9f * scale)

        // Shin Guards
        scope.drawLine(specularShine, Offset(legLeftX, feetPt.y - 12f * scale), Offset(legLeftX, feetPt.y - 2f * scale), strokeWidth = 7f * scale)
        scope.drawLine(specularShine, Offset(legRightX, feetPt.y - 12f * scale), Offset(legRightX, feetPt.y - 2f * scale), strokeWidth = 7f * scale)

        // 3. Torso & Ballistic Vest Level 3/4 Visible
        val torsoWidth = 22f * scale
        val torsoHeight = 28f * scale
        val torsoTopY = torsoPt.y - torsoHeight / 2f + crouchOffset

        // Base torso outline & fill
        scope.drawRect(outlineColor, topLeft = Offset(torsoPt.x - torsoWidth / 2f - 2f * scale, torsoTopY - 2f * scale), size = androidx.compose.ui.geometry.Size(torsoWidth + 4f * scale, torsoHeight + 4f * scale))
        scope.drawRect(armorColor, topLeft = Offset(torsoPt.x - torsoWidth / 2f, torsoTopY), size = androidx.compose.ui.geometry.Size(torsoWidth, torsoHeight))

        // Specular Gold Shine Reflection
        scope.drawLine(specularShine, Offset(torsoPt.x - torsoWidth / 2f + 4f * scale, torsoTopY + 4f * scale), Offset(torsoPt.x - torsoWidth / 2f + 8f * scale, torsoTopY + torsoHeight - 4f * scale), strokeWidth = 3f * scale)

        // Vest Level Badge on Chest (Vest Level 3)
        scope.drawRect(
            color = if (isPlayer) Color(0xFF00E5FF) else Color(0xFFFF5252),
            topLeft = Offset(torsoPt.x - 5f * scale, torsoTopY + 6f * scale),
            size = androidx.compose.ui.geometry.Size(10f * scale, 7f * scale)
        )

        // 4. Arms & Weapons
        val armLeftX = torsoPt.x - torsoWidth / 2f - 4f * scale
        val armRightX = torsoPt.x + torsoWidth / 2f + 4f * scale
        val armY = torsoTopY + 6f * scale

        val isAttacking = char.animState == CharacterAnimationState.SLASH || char.animState == CharacterAnimationState.SHOOT
        val attackSwing = if (isAttacking) sin(animTime * 25f) * 16f * scale else 0f

        // Left Arm holding gun grip / barrel
        scope.drawLine(outlineColor, Offset(armLeftX, armY), Offset(armLeftX + 10f * scale, armY + 16f * scale), strokeWidth = 9f * scale)
        scope.drawLine(armorColor, Offset(armLeftX, armY), Offset(armLeftX + 10f * scale, armY + 16f * scale), strokeWidth = 7f * scale)

        // Right Arm (Trigger hand)
        val handRightX = armRightX + 4f * scale + attackSwing
        val handRightY = armY + 14f * scale
        scope.drawLine(outlineColor, Offset(armRightX, armY), Offset(handRightX, handRightY), strokeWidth = 9f * scale)
        scope.drawLine(armorColor, Offset(armRightX, armY), Offset(handRightX, handRightY), strokeWidth = 7f * scale)

        // REALISTIC 1-METER AK-47 RIFLE OR GOLD KATANA IN HAND
        if (char.equippedWeapon.category == WeaponCategory.FIREARM || char.equippedWeapon.id.contains("ak")) {
            // Real 1m AK-47: Long barrel, wooden stock, curved magazine, muzzle
            val gunStart = Offset(handRightX - 10f * scale, handRightY + 4f * scale)
            val gunEnd = Offset(handRightX + 32f * scale, handRightY - 5f * scale)

            // Stock
            scope.drawLine(Color(0xFF8D6E63), gunStart, Offset(handRightX, handRightY), strokeWidth = 6.5f * scale)
            // Steel Receiver & Barrel
            scope.drawLine(Color(0xFF263238), Offset(handRightX - 2f * scale, handRightY), gunEnd, strokeWidth = 5.5f * scale)
            // Gold Trim
            scope.drawLine(Color(0xFFFFD700), Offset(handRightX, handRightY - 1f * scale), Offset(gunEnd.x - 8f * scale, gunEnd.y), strokeWidth = 2f * scale)
            // Curved Banana Magazine
            scope.drawLine(Color(0xFF1E2833), Offset(handRightX + 6f * scale, handRightY + 2f * scale), Offset(handRightX + 8f * scale, handRightY + 12f * scale), strokeWidth = 4f * scale)
            // 4x Scope on Top
            scope.drawRect(Color(0xFF37474F), topLeft = Offset(handRightX + 4f * scale, handRightY - 8f * scale), size = androidx.compose.ui.geometry.Size(12f * scale, 4f * scale))

            // MUZZLE FLASH STARBURST (When shooting!)
            if (isAttacking) {
                scope.drawCircle(Color(0xFFFFEE58), 12f * scale, gunEnd)
                scope.drawCircle(Color(0xFFFF5722), 6f * scale, gunEnd)
            }
        } else {
            // Big Gleaming 1.2m Gold Katana Blade
            val bladeEnd = Offset(handRightX + 22f * scale, handRightY - 38f * scale)
            scope.drawLine(Color(0xFF1A1A1A), Offset(handRightX, handRightY), bladeEnd, strokeWidth = 6f * scale)
            scope.drawLine(Color(0xFFFFD700), Offset(handRightX, handRightY), bladeEnd, strokeWidth = 4.5f * scale)
            scope.drawLine(Color(0xFFFFFFFF), Offset(handRightX, handRightY), bladeEnd, strokeWidth = 1.5f * scale)
            scope.drawCircle(Color(0xFFFFD700), 5.5f * scale, Offset(handRightX, handRightY))
        }

        // Crossed Katanas on Back
        scope.drawLine(outlineColor, Offset(torsoPt.x - 14f * scale, torsoTopY - 8f * scale), Offset(torsoPt.x + 12f * scale, torsoTopY + 20f * scale), strokeWidth = 4f * scale)
        scope.drawLine(Color(0xFFFFD700), Offset(torsoPt.x - 14f * scale, torsoTopY - 8f * scale), Offset(torsoPt.x + 12f * scale, torsoTopY + 20f * scale), strokeWidth = 2.5f * scale)

        // 5. Head & Level 3 Helmet with Golden Visor
        val headRadius = 12f * scale
        val headCenterY = headPt.y + headRadius + crouchOffset

        // Helmet Level 3
        scope.drawCircle(outlineColor, headRadius + 2f * scale, Offset(headPt.x, headCenterY))
        scope.drawCircle(armorColor, headRadius, Offset(headPt.x, headCenterY))

        // Visor & Glowing Eyes
        val eyeWidth = 6f * scale
        val eyeHeight = 3f * scale
        val eyeY = headCenterY - 1.5f * scale

        scope.drawOval(Color(0xFFFFF59D), Offset(headPt.x - 7f * scale, eyeY), androidx.compose.ui.geometry.Size(eyeWidth, eyeHeight))
        scope.drawOval(Color(0xFFFFF59D), Offset(headPt.x + 1.5f * scale, eyeY), androidx.compose.ui.geometry.Size(eyeWidth, eyeHeight))
        scope.drawCircle(Color(0xFFFFFFFF), 1.8f * scale, Offset(headPt.x - 4f * scale, eyeY + 1.2f * scale))
        scope.drawCircle(Color(0xFFFFFFFF), 1.8f * scale, Offset(headPt.x + 4f * scale, eyeY + 1.2f * scale))

        // 6. HEALTH BAR & NAMEPLATE (Cleanly positioned above head, never blocking body)
        if (depth < 50f) {
            val nameplateY = headPt.y - 16f * scale
            val barWidth = 60f * scale
            val barHeight = 6.5f * scale

            scope.drawRect(Color(0xCC000000), topLeft = Offset(headPt.x - barWidth / 2f, nameplateY), size = androidx.compose.ui.geometry.Size(barWidth, barHeight))

            val hpRatio = (char.health / char.maxHealth).coerceIn(0f, 1f)
            val hpColor = if (isPlayer) Color(0xFF00E676) else Color(0xFFFF1744)
            scope.drawRect(hpColor, topLeft = Offset(headPt.x - barWidth / 2f, nameplateY), size = androidx.compose.ui.geometry.Size(barWidth * hpRatio, barHeight))

            val paint = Paint().apply {
                color = if (isPlayer) android.graphics.Color.YELLOW else android.graphics.Color.RED
                textSize = (13f * 2.2f * scale).coerceIn(18f, 32f)
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
            scope.drawContext.canvas.nativeCanvas.drawText(
                if (isPlayer) "⚡ ${char.name} [LVL 3 VEST/HELM]" else "☠ ${char.name}",
                headPt.x,
                nameplateY - 6f,
                paint
            )
        }
    }

    private fun drawTigerCompanion(scope: DrawScope, tiger: TigerCompanion, camera: Camera3D, w: Float, h: Float, animTime: Float) {
        val pt = projectToScreen(tiger.position, camera, w, h) ?: return
        val depth = pt.depth.coerceAtLeast(0.5f)
        val scale = (60f / depth).coerceIn(1.2f, 4.0f)
        val tigerColor = Color(0xFFFF9100)

        scope.drawOval(tigerColor, topLeft = Offset(pt.x - 16f * scale, pt.y - 12f * scale), size = androidx.compose.ui.geometry.Size(32f * scale, 18f * scale))
        for (i in -1..1) {
            scope.drawLine(Color(0xFF212121), Offset(pt.x + i * 8f * scale, pt.y - 12f * scale), Offset(pt.x + i * 8f * scale, pt.y), strokeWidth = 3f * scale)
        }
        scope.drawCircle(tigerColor, 10f * scale, Offset(pt.x + 16f * scale, pt.y - 6f * scale))
        scope.drawCircle(Color(0xFFFFD700), 2.5f * scale, Offset(pt.x + 20f * scale, pt.y - 2f * scale))

        if (depth < 35f) {
            val paint = Paint().apply {
                color = android.graphics.Color.rgb(255, 145, 0)
                textSize = 20f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
            scope.drawContext.canvas.nativeCanvas.drawText("🐯 ${tiger.name}", pt.x, pt.y - 20f * scale, paint)
        }
    }

    private fun drawSlashEffect(scope: DrawScope, slash: SlashEffect3D, camera: Camera3D, w: Float, h: Float) {
        val p1 = projectToScreen(slash.origin, camera, w, h) ?: return
        val p2 = projectToScreen(slash.origin + slash.direction * 4.5f, camera, w, h) ?: return

        scope.drawLine(Color(0xFFFFD700), Offset(p1.x, p1.y), Offset(p2.x, p2.y), strokeWidth = 8f)
        scope.drawLine(Color(0xFFFFFFFF), Offset(p1.x, p1.y), Offset(p2.x, p2.y), strokeWidth = 3f)
    }

    private fun drawProjectile(scope: DrawScope, proj: Projectile3D, camera: Camera3D, w: Float, h: Float) {
        val pt = projectToScreen(proj.position, camera, w, h) ?: return
        val trailPt = projectToScreen(proj.position - proj.velocity.normalized() * 3.5f, camera, w, h)

        if (trailPt != null) {
            // Golden Bullet Tracer Trail
            scope.drawLine(Color(0xFFFFD700), Offset(pt.x, pt.y), Offset(trailPt.x, trailPt.y), strokeWidth = 4.5f)
        }
        scope.drawCircle(Color(0xFFFFD54F), 6f, Offset(pt.x, pt.y))
        scope.drawCircle(Color(0xFFFFFFFF), 3f, Offset(pt.x, pt.y))
    }

    private fun drawFloatingDamage(scope: DrawScope, dmg: FloatingDamageText, camera: Camera3D, w: Float, h: Float) {
        val pt = projectToScreen(dmg.position, camera, w, h) ?: return
        val paint = Paint().apply {
            color = if (dmg.isHeadshot) android.graphics.Color.rgb(255, 215, 0) else android.graphics.Color.RED
            textSize = if (dmg.isHeadshot) 42f else 28f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        // Exact requested Headshot Gold text: "HEADSHOT 250"
        val text = if (dmg.isHeadshot) "💥 HEADSHOT 250" else "-${dmg.damage.toInt()}"

        // Shadow outline
        val outlinePaint = Paint(paint).apply {
            color = android.graphics.Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        scope.drawContext.canvas.nativeCanvas.drawText(text, pt.x, pt.y, outlinePaint)
        scope.drawContext.canvas.nativeCanvas.drawText(text, pt.x, pt.y, paint)
    }
}
