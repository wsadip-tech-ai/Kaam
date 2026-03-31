package com.kaam.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun KaamNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // Auth flow
        composable(Screen.Welcome.route) {
            PlaceholderScreen("Welcome")
        }
        composable(Screen.PhoneEntry.route) {
            PlaceholderScreen("Phone Entry")
        }
        composable(Screen.RoleSelect.route) {
            PlaceholderScreen("Role Select")
        }
        composable(Screen.BasicInfo.route) {
            PlaceholderScreen("Basic Info")
        }

        // Customer
        composable(Screen.CustomerHome.route) {
            PlaceholderScreen("Customer Home")
        }
        composable(Screen.MyBookings.route) {
            PlaceholderScreen("My Bookings")
        }
        composable(Screen.PostJob.route) {
            PlaceholderScreen("Post Job")
        }

        // Worker
        composable(Screen.WorkerDashboard.route) {
            PlaceholderScreen("Worker Dashboard")
        }
        composable(Screen.JobBoard.route) {
            PlaceholderScreen("Job Board")
        }
        composable(Screen.ProfileSetup.route) {
            PlaceholderScreen("Profile Setup")
        }
        composable(Screen.VerificationStatus.route) {
            PlaceholderScreen("Verification Status")
        }

        // Shared
        composable(Screen.ChatList.route) {
            PlaceholderScreen("Chat List")
        }
        composable(Screen.Settings.route) {
            PlaceholderScreen("Settings")
        }
        composable(Screen.Notifications.route) {
            PlaceholderScreen("Notifications")
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = name)
    }
}
