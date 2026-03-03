package com.codex.torrentx

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codex.torrentx.ui.screens.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UniversalHomeActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val sharedPrefs = getSharedPreferences("TorrentX_Prefs", Context.MODE_PRIVATE)
            var hasSeenOnboarding by remember {
                mutableStateOf(sharedPrefs.getBoolean("has_seen_onboarding_v2", false))
            }

            // ── Force Update Check ──
            var updateResult by remember { mutableStateOf<com.codex.torrentx.util.ForceUpdateChecker.UpdateResult?>(null) }
            var showUpdateDialog by remember { mutableStateOf(false) }
            val currentVersion = remember { com.codex.torrentx.util.ForceUpdateChecker.getVersionName(this@UniversalHomeActivity) }

            LaunchedEffect(Unit) {
                val result = com.codex.torrentx.util.ForceUpdateChecker.checkForUpdate(this@UniversalHomeActivity)
                updateResult = result
                if (result is com.codex.torrentx.util.ForceUpdateChecker.UpdateResult.UpdateAvailable) {
                    showUpdateDialog = true
                }
            }

            // Show Force Update Dialog if needed
            if (showUpdateDialog) {
                val result = updateResult
                if (result is com.codex.torrentx.util.ForceUpdateChecker.UpdateResult.UpdateAvailable) {
                    com.codex.torrentx.ui.components.ForceUpdateDialog(
                        config = result.config,
                        isMandatory = result.isMandatory,
                        currentVersion = currentVersion,
                        onDismiss = { showUpdateDialog = false }
                    )
                }
            }
            
            // ── V1.0.1 Changelog Popup ──
            var showChangelog by remember { mutableStateOf(!sharedPrefs.getBoolean("has_seen_changelog_1_0_1", false)) }
            if (showChangelog && !showUpdateDialog && hasSeenOnboarding) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = {
                        sharedPrefs.edit().putBoolean("has_seen_changelog_1_0_1", true).apply()
                        showChangelog = false
                    },
                    title = {
                        androidx.compose.material3.Text("What's New in 8XDL V1.0.1", androidx.compose.ui.text.font.FontWeight.Bold)
                    },
                    text = {
                        val scrollState = androidx.compose.foundation.rememberScrollState()
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.androidx.compose.foundation.verticalScroll(scrollState)
                        ) {
                            androidx.compose.material3.Text(
                                "🚀 Extensive UI and Engine improvements!\n\n" +
                                "• VOD Engine Analytics: Real-time duration, codec details, & size estimates tracking in M3U8X cards.\n" +
                                "• Smart Dark Mode: Overhauled browser inversion logic ensures videos and standard images retain their natural original colors.\n" +
                                "• Intrusion Blocker: Built-in JavaScript dialog mitigation preventing infinite loops and spam alerts on heavy ad-sites.\n" +
                                "• Import Identity: Novel browser tool enabling simple injection of arbitrary cookies formats to bypass harsh web protections.\n" +
                                "• Agnostic AdBlocker: Deep signature blocking targets thousands of new generic sponsor spaces structurally.\n" +
                                "• Performance Bump: Re-threaded components leveraging the fast Media3 ExoPlayer modules.",
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    confirmButton = {
                        androidx.compose.material3.TextButton(
                            onClick = {
                                sharedPrefs.edit().putBoolean("has_seen_changelog_1_0_1", true).apply()
                                showChangelog = false
                            }
                        ) {
                            androidx.compose.material3.Text("Got it")
                        }
                    }
                )
            }

            if (!hasSeenOnboarding) {
                com.codex.torrentx.ui.screens.OnboardingScreen(
                    onFinished = {
                        sharedPrefs.edit().putBoolean("has_seen_onboarding_v2", true).apply()
                        hasSeenOnboarding = true
                    }
                )
            } else {
                val navController = rememberNavController()
                
                // Create ViewModel at NavHost level so it persists across navigation
                val vaultViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.codex.torrentx.ui.vault.VaultViewModel>()
                
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onTorrentXClick = {
                                startActivity(Intent(this@UniversalHomeActivity, TorrentDashboardActivity::class.java))
                            },
                            onDownloadXClick = {
                                startActivity(Intent(this@UniversalHomeActivity, DownloadXActivity::class.java))
                            },
                            onM3u8XClick = {
                                startActivity(Intent(this@UniversalHomeActivity, M3u8Activity::class.java))
                            },
                            onVideoXClick = {
                                startActivity(Intent(this@UniversalHomeActivity, VideoXActivity::class.java))
                            },
                            onMusicXClick = {
                                startActivity(Intent(this@UniversalHomeActivity, MusicXActivity::class.java))
                            },
                            onBrowserClick = {
                                startActivity(Intent(this@UniversalHomeActivity, com.browserx.app.MainActivity::class.java))
                            },
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                    composable("feedx") { com.codex.torrentx.ui.feed.FeedsScreen(onBack = { navController.popBackStack() }) }
                    composable("vaultx") {
                        // Check state and navigate if needed
                        LaunchedEffect(Unit) {
                            when {
                                !vaultViewModel.isSetupComplete -> {
                                    navController.navigate("vault_setup") {
                                        popUpTo("vaultx") { inclusive = true }
                                    }
                                }
                                !vaultViewModel.isUnlocked -> {
                                    navController.navigate("vault_unlock") {
                                        popUpTo("vaultx") { inclusive = true }
                                    }
                                }
                            }
                        }
                        
                        // Show vault screen
                        com.codex.torrentx.ui.vault.VaultScreen(
                            onBack = { 
                                navController.popBackStack() 
                            },
                            onNavigateToSettings = {
                                navController.navigate("vault_settings")
                            },
                            viewModel = vaultViewModel
                        )
                    }
                    composable("vault_settings") {
                        com.codex.torrentx.ui.vault.VaultSettingsScreen(
                            onBack = { navController.popBackStack() },
                            viewModel = vaultViewModel
                        )
                    }
                    composable("vault_setup") {
                        com.codex.torrentx.ui.vault.VaultSetupScreenNew(
                            onSetupComplete = {
                                // After setup, go back to home so user can test unlock
                                navController.navigate("home") {
                                    popUpTo("vault_setup") { inclusive = true }
                                }
                            },
                            viewModel = vaultViewModel
                        )
                    }
                    composable("vault_unlock") {
                        com.codex.torrentx.ui.vault.VaultUnlockScreenNew(
                            onUnlocked = {
                                navController.navigate("vaultx") {
                                    popUpTo("vault_unlock") { inclusive = true }
                                }
                            },
                            viewModel = vaultViewModel
                        )
                    }
                    composable("guide") { com.codex.torrentx.ui.screens.GuideScreen(onBack = { navController.popBackStack() }) }
                    composable("updates") { com.codex.torrentx.ui.screens.UpdatesScreen(onBack = { navController.popBackStack() }) }
                    composable("share") { com.codex.torrentx.ui.screens.ShareScreen(onBack = { navController.popBackStack() }) }
                    composable("credits") { com.codex.torrentx.ui.screens.CreditsScreen(onBack = { navController.popBackStack() }) }
                    composable("bug_report") { com.codex.torrentx.ui.screens.BugReportScreen(onBack = { navController.popBackStack() }) }
                    composable("feature_request") { com.codex.torrentx.ui.screens.FeatureRequestScreen(onBack = { navController.popBackStack() }) }
                    composable("website") { com.codex.torrentx.ui.screens.WebsiteScreen(onBack = { navController.popBackStack() }) }
                    composable("google_privacy") { com.codex.torrentx.ui.screens.GooglePrivacyScreen(onBack = { navController.popBackStack() }) }
                    composable("privacy_policy") { com.codex.torrentx.ui.screens.PrivacyPolicyScreen(onBack = { navController.popBackStack() }) }
                    composable("disclaimer") { com.codex.torrentx.ui.screens.DisclaimerSettingsScreen(onBack = { navController.popBackStack() }) }
                }
            }
        }
    }
}
