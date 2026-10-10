package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.engine.AudioManager
import com.example.engine.GameEngine
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
    LOBBY,
    GAME,
    SHOP,
    SHADOW_PASS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val audioManager = AudioManager(applicationContext)
        val gameEngine = GameEngine(audioManager)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(AppScreen.GAME) }
                    var showPetDialog by remember { mutableStateOf(false) }
                    var showGkJutsuDialog by remember { mutableStateOf(false) }
                    var showDailyDialog by remember { mutableStateOf(false) }
                    var showClansDialog by remember { mutableStateOf(false) }
                    var showSpinDialog by remember { mutableStateOf(false) }
                    var showSettingsDialog by remember { mutableStateOf(false) }
                    var hudVersion by remember { mutableIntStateOf(0) }

                    // Navigation BackHandler
                    BackHandler(enabled = currentScreen != AppScreen.LOBBY || showPetDialog || showGkJutsuDialog || showDailyDialog || showClansDialog || showSpinDialog || showSettingsDialog) {
                        when {
                            showPetDialog -> showPetDialog = false
                            showGkJutsuDialog -> showGkJutsuDialog = false
                            showDailyDialog -> showDailyDialog = false
                            showClansDialog -> showClansDialog = false
                            showSpinDialog -> showSpinDialog = false
                            showSettingsDialog -> {
                                showSettingsDialog = false
                                hudVersion++
                            }
                            currentScreen != AppScreen.LOBBY -> currentScreen = AppScreen.LOBBY
                        }
                    }

                    when (currentScreen) {
                        AppScreen.LOBBY -> {
                            LobbyScreen(
                                engine = gameEngine,
                                onStartGame = {
                                    gameEngine.initMatch()
                                    currentScreen = AppScreen.GAME
                                },
                                onOpenShop = { currentScreen = AppScreen.SHOP },
                                onOpenPass = { currentScreen = AppScreen.SHADOW_PASS },
                                onOpenPet = { showPetDialog = true },
                                onOpenGk = { showGkJutsuDialog = true },
                                onOpenDaily = { showDailyDialog = true },
                                onOpenClans = { showClansDialog = true },
                                onOpenSpin = { showSpinDialog = true },
                                onOpenSettings = { showSettingsDialog = true }
                            )
                        }
                        AppScreen.GAME -> {
                            key(hudVersion) {
                                GameScreen(
                                    engine = gameEngine,
                                    onReturnToLobby = { currentScreen = AppScreen.LOBBY },
                                    onOpenGkJutsu = { showGkJutsuDialog = true },
                                    onOpenSettings = { showSettingsDialog = true }
                                )
                            }
                        }
                        AppScreen.SHOP -> {
                            ShopScreen(
                                engine = gameEngine,
                                onBack = { currentScreen = AppScreen.LOBBY }
                            )
                        }
                        AppScreen.SHADOW_PASS -> {
                            ShadowPassScreen(
                                onBack = { currentScreen = AppScreen.LOBBY }
                            )
                        }
                    }

                    // Modals
                    if (showPetDialog) {
                        TigerPetDialog(engine = gameEngine, onDismiss = { showPetDialog = false })
                    }
                    if (showGkJutsuDialog) {
                        BanglaGkJutsuDialog(engine = gameEngine, onDismiss = { showGkJutsuDialog = false })
                    }
                    if (showDailyDialog) {
                        DailyRewardDialog(engine = gameEngine, onDismiss = { showDailyDialog = false })
                    }
                    if (showClansDialog) {
                        ClansDialog(onDismiss = { showClansDialog = false })
                    }
                    if (showSpinDialog) {
                        SpinWheelDialog(engine = gameEngine, onDismiss = { showSpinDialog = false })
                    }
                    if (showSettingsDialog) {
                        SettingsDialog(engine = gameEngine, onDismiss = {
                            showSettingsDialog = false
                            hudVersion++
                        })
                    }
                }
            }
        }
    }
}
