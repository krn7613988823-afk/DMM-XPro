package com.example.engine

import com.example.model.Building3D
import com.example.model.Vector3
import kotlin.math.cos
import kotlin.math.sin

class Camera3D(
    var targetPosition: Vector3 = Vector3(0f, 1.3f, 0f),
    var yawDegrees: Float = 0f,
    var pitchDegrees: Float = 7f,
    var targetDistance: Float = 4.8f,  // 5 meters behind ninja over the shoulder
    var currentDistance: Float = 4.8f,
    var shoulderOffset: Float = 0.45f, // Over the right shoulder (Free Fire style)
    var heightOffset: Float = 1.6f,
    var aimSensitivity: Float = 1.0f,
    var isAimingDownSights: Boolean = false
) {
    var position: Vector3 = Vector3(0f, 2.0f, -4.8f)
        private set

    fun update(target: Vector3, buildings: List<Building3D>, deltaTime: Float) {
        val desiredDist = if (isAimingDownSights) 2.0f else targetDistance
        val smoothFactor = (14f * deltaTime).coerceIn(0.01f, 1f)
        currentDistance += (desiredDist - currentDistance) * smoothFactor

        val yawRad = Math.toRadians(yawDegrees.toDouble()).toFloat()
        val pitchRad = Math.toRadians(pitchDegrees.toDouble()).toFloat()

        // Spherical offsets from player target
        val hDist = currentDistance * cos(pitchRad)
        val vDist = currentDistance * sin(pitchRad)

        // Direction from target to camera (backwards)
        val dirX = -sin(yawRad) * hDist
        val dirZ = -cos(yawRad) * hDist
        val dirY = vDist + heightOffset

        // Right shoulder vector
        val rightX = cos(yawRad) * shoulderOffset
        val rightZ = -sin(yawRad) * shoulderOffset

        var idealPos = Vector3(
            target.x + dirX + rightX,
            (target.y + dirY).coerceAtLeast(0.6f),
            target.z + dirZ + rightZ
        )

        // Collision avoidance: check if camera penetrates building bounds
        var safeDist = currentDistance
        for (b in buildings) {
            val minX = b.position.x - b.width / 2f
            val maxX = b.position.x + b.width / 2f
            val minZ = b.position.z - b.depth / 2f
            val maxZ = b.position.z + b.depth / 2f
            val maxY = b.position.y + b.height

            if (idealPos.x in minX..maxX && idealPos.z in minZ..maxZ && idealPos.y < maxY) {
                safeDist = (safeDist * 0.6f).coerceAtLeast(1.5f)
            }
        }

        if (safeDist < currentDistance) {
            val actualHDist = safeDist * cos(pitchRad)
            val actualVDist = safeDist * sin(pitchRad)
            idealPos = Vector3(
                target.x - sin(yawRad) * actualHDist + rightX * 0.3f,
                (target.y + actualVDist + heightOffset * 0.8f).coerceAtLeast(0.6f),
                target.z - cos(yawRad) * actualHDist + rightZ * 0.3f
            )
        }

        position = Vector3.lerp(position, idealPos, smoothFactor)
    }

    fun rotate(deltaYaw: Float, deltaPitch: Float) {
        yawDegrees = (yawDegrees + deltaYaw * aimSensitivity) % 360f
        if (yawDegrees < 0f) yawDegrees += 360f
        pitchDegrees = (pitchDegrees + deltaPitch * aimSensitivity).coerceIn(-25f, 50f)
    }

    fun toggleShoulder() {
        shoulderOffset = -shoulderOffset
    }

    fun getForward(): Vector3 {
        val yawRad = Math.toRadians(yawDegrees.toDouble()).toFloat()
        return Vector3(sin(yawRad), 0f, cos(yawRad)).normalized()
    }

    fun getRight(): Vector3 {
        val yawRad = Math.toRadians(yawDegrees.toDouble()).toFloat()
        return Vector3(cos(yawRad), 0f, -sin(yawRad)).normalized()
    }
}
