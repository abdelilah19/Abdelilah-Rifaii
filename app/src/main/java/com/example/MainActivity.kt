package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.data.localization.AppLanguage
import com.example.ui.screens.*
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NetworkViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NetworkViewModel = viewModel()
            
            MyApplicationTheme(darkTheme = viewModel.isDarkMode) {
                // Instantly support Right-to-Left (RTL) layout when Arabic language is selected
                val layoutDir = if (viewModel.currentLanguage == AppLanguage.AR) {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }
                
                CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                    AppNavigationContainer(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigationContainer(viewModel: NetworkViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationBarItems = listOf(
        Triple("home", viewModel.getString("home"), Icons.Default.Home),
        Triple("scanner", viewModel.getString("net_scan"), Icons.Default.Radar),
        Triple("tools", viewModel.getString("net_tools"), Icons.Default.Handyman),
        Triple("ai_chat", viewModel.getString("ai_diag"), Icons.Default.Psychology),
        Triple("settings", viewModel.getString("settings"), Icons.Default.Settings)
    )

    // Hide navigation margins and bars for splash and webview screens
    val isBottomBarVisible = currentRoute != "splash" && currentRoute != "router"

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding().testTag("app_bottom_bar")
                ) {
                    navigationBarItems.forEach { item ->
                        val isSelected = currentRoute == item.first
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.first) {
                                    navController.navigate(item.first) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.third,
                                    contentDescription = item.second,
                                    tint = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            },
                            label = {
                                Text(
                                    text = item.second,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = ElectricBlue.copy(alpha = 0.08f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(
                bottom = if (isBottomBarVisible) padding.calculateBottomPadding() else 0.dp
            )
        ) {
            composable("splash") {
                SplashScreen(
                    appName = viewModel.getString("app_name"),
                    onSplashFinished = {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToRoute = { route ->
                        navController.navigate(route)
                    }
                )
            }

            composable("scanner") {
                NetworkScannerScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("tools") {
                // Secondary tools screen (Ping, DNS Lookup, Port Scanner, WiFi Analyzer)
                ToolsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable("ai_chat") {
                AiDiagnosticsChatScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Embedded screens targets
            composable("router") {
                RouterWebViewScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("speedtest") {
                SpeedTestScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
