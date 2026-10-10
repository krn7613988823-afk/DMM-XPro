package com.example.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * High-performance 3D Vector for Game Engine physics, coordinates, and projection.
 */
data class Vector3(
    var x: Float = 0f,
    var y: Float = 0f, // Up axis in game coordinates
    var z: Float = 0f  // Forward/Depth axis
) {
    operator fun plus(other: Vector3): Vector3 = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3): Vector3 = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float): Vector3 = Vector3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float): Vector3 = if (scalar != 0f) Vector3(x / scalar, y / scalar, z / scalar) else Vector3()

    fun lengthSquared(): Float = x * x + y * y + z * z
    fun length(): Float = sqrt(lengthSquared())

    fun distanceTo(other: Vector3): Float = (this - other).length()
    fun distance2D(other: Vector3): Float = sqrt((x - other.x) * (x - other.x) + (z - other.z) * (z - other.z))

    fun normalized(): Vector3 {
        val len = length()
        return if (len > 0.0001f) Vector3(x / len, y / len, z / len) else Vector3(0f, 0f, 0f)
    }

    fun dot(other: Vector3): Float = x * other.x + y * other.y + z * other.z

    fun rotateY(angleRad: Float): Vector3 {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vector3(
            x * cosA - z * sinA,
            y,
            x * sinA + z * cosA
        )
    }

    companion object {
        val ZERO get() = Vector3(0f, 0f, 0f)
        val UP get() = Vector3(0f, 1f, 0f)
        val FORWARD get() = Vector3(0f, 0f, 1f)
        val RIGHT get() = Vector3(1f, 0f, 0f)

        fun lerp(a: Vector3, b: Vector3, t: Float): Vector3 {
            val clampedT = t.coerceIn(0f, 1f)
            return Vector3(
                a.x + (b.x - a.x) * clampedT,
                a.y + (b.y - a.y) * clampedT,
                a.z + (b.z - a.z) * clampedT
            )
        }
    }
}
