package com.kaam.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaam.app.navigation.BottomNavBar
import com.kaam.app.navigation.BottomNavItem
import com.kaam.app.navigation.KaamNavHost
import com.kaam.app.navigation.Screen
import com.kaam.app.ui.theme.KaamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaamTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val customerRoutes = setOf(
                    Screen.CustomerHome.route,
                    Screen.MyBookings.route,
                    Screen.ChatList.route,
                    Screen.Settings.route,
                )
                val workerRoutes = setOf(
                    Screen.WorkerDashboard.route,
                    Screen.JobBoard.route,
                    Screen.ChatList.route,
                    Screen.Settings.route,
                )
                val authRoutes = setOf(
                    Screen.Welcome.route,
                    Screen.PhoneEntry.route,
                    Screen.OtpVerify.route,
                    Screen.RoleSelect.route,
                    Screen.BasicInfo.route,
                    Screen.ProfileSetup.route,
                    Screen.VerificationStatus.route,
                )

                val showBottomBar = currentRoute != null && currentRoute !in authRoutes
                val isWorker = currentRoute in workerRoutes && currentRoute !in customerRoutes

                val bottomNavItems = if (isWorker) {
                    listOf(
                        BottomNavItem.WorkerDashboard,
                        BottomNavItem.WorkerJobs,
                        BottomNavItem.WorkerChat,
                        BottomNavItem.WorkerProfile,
                    )
                } else {
                    listOf(
                        BottomNavItem.CustomerHome,
                        BottomNavItem.CustomerBookings,
                        BottomNavItem.CustomerChat,
                        BottomNavItem.CustomerProfile,
                    )
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(
                                items = bottomNavItems,
                                currentRoute = currentRoute,
                                onItemClick = { item ->
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            )
                        }
                    },
                ) { paddingValues ->
                    KaamNavHost(
                        navController = navController,
                        startDestination = Screen.Welcome.route,
                        modifier = Modifier.padding(paddingValues),
                    )
                }
            }
        }
    }
}
