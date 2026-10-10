package com.example.model

import android.content.Context
import android.content.SharedPreferences

data class HudElementConfig(
    var xPercent: Float,     // 0f (left) to 1f (right)
    var yPercent: Float,     // 0f (top) to 1f (bottom)
    var sizeDp: Float,       // 50f to 200f
    var alpha: Float = 0.9f  // 0.3f to 1.0f
)

class CustomHudConfig(
    var joystick: HudElementConfig = HudElementConfig(0.12f, 0.75f, 130f, 0.9f),
    var fireBtn: HudElementConfig = HudElementConfig(0.88f, 0.75f, 100f, 1.0f),
    var jumpBtn: HudElementConfig = HudElementConfig(0.92f, 0.52f, 64f, 0.9f),
    var dashBtn: HudElementConfig = HudElementConfig(0.78f, 0.84f, 64f, 0.9f),
    var crouchBtn: HudElementConfig = HudElementConfig(0.78f, 0.68f, 56f, 0.9f),
    var blockBtn: HudElementConfig = HudElementConfig(0.85f, 0.52f, 56f, 0.9f),
    var reloadBtn: HudElementConfig = HudElementConfig(0.74f, 0.48f, 54f, 0.9f),
    var scopeBtn: HudElementConfig = HudElementConfig(0.84f, 0.36f, 54f, 0.9f),
    var medkitBtn: HudElementConfig = HudElementConfig(0.08f, 0.48f, 54f, 0.9f)
) {
    fun save(context: Context) {
        val sp: SharedPreferences = context.getSharedPreferences("freefire_hud_prefs", Context.MODE_PRIVATE)
        val editor = sp.edit()
        fun saveElem(key: String, elem: HudElementConfig) {
            editor.putFloat("${key}_x", elem.xPercent)
            editor.putFloat("${key}_y", elem.yPercent)
            editor.putFloat("${key}_size", elem.sizeDp)
            editor.putFloat("${key}_alpha", elem.alpha)
        }
        saveElem("joystick", joystick)
        saveElem("fire", fireBtn)
        saveElem("jump", jumpBtn)
        saveElem("dash", dashBtn)
        saveElem("crouch", crouchBtn)
        saveElem("block", blockBtn)
        saveElem("reload", reloadBtn)
        saveElem("scope", scopeBtn)
        saveElem("medkit", medkitBtn)
        editor.apply()
    }

    companion object {
        fun load(context: Context): CustomHudConfig {
            val sp = context.getSharedPreferences("freefire_hud_prefs", Context.MODE_PRIVATE)
            if (!sp.contains("fire_x")) {
                return CustomHudConfig()
            }
            fun loadElem(key: String, defX: Float, defY: Float, defSize: Float, defAlpha: Float): HudElementConfig {
                return HudElementConfig(
                    sp.getFloat("${key}_x", defX),
                    sp.getFloat("${key}_y", defY),
                    sp.getFloat("${key}_size", defSize),
                    sp.getFloat("${key}_alpha", defAlpha)
                )
            }
            return CustomHudConfig(
                joystick = loadElem("joystick", 0.12f, 0.75f, 130f, 0.9f),
                fireBtn = loadElem("fire", 0.88f, 0.75f, 100f, 1.0f),
                jumpBtn = loadElem("jump", 0.92f, 0.52f, 64f, 0.9f),
                dashBtn = loadElem("dash", 0.78f, 0.84f, 64f, 0.9f),
                crouchBtn = loadElem("crouch", 0.78f, 0.68f, 56f, 0.9f),
                blockBtn = loadElem("block", 0.85f, 0.52f, 56f, 0.9f),
                reloadBtn = loadElem("reload", 0.74f, 0.48f, 54f, 0.9f),
                scopeBtn = loadElem("scope", 0.84f, 0.36f, 54f, 0.9f),
                medkitBtn = loadElem("medkit", 0.08f, 0.48f, 54f, 0.9f)
            )
        }

        fun default(): CustomHudConfig = CustomHudConfig()
    }
}
