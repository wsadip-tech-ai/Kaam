package com.kaam.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kaam.app.data.model.Profile
import com.kaam.app.data.model.UserRole
import com.kaam.app.ui.auth.AuthViewModel
import com.kaam.app.ui.auth.BasicInfoScreen
import com.kaam.app.ui.auth.OtpVerifyScreen
import com.kaam.app.ui.auth.PhoneEntryScreen
import com.kaam.app.ui.auth.RoleSelectScreen
import com.kaam.app.ui.auth.WelcomeScreen

@Composable
fun KaamNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    // Hoist the AuthViewModel at NavHost level so auth state persists across auth screens
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // ── Auth flow ──────────────────────────────────────────────────────────

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Screen.PhoneEntry.route)
                },
            )
        }

        composable(Screen.PhoneEntry.route) {
            // Navigate forward once OTP has been sent
            LaunchedEffect(uiState.otpSent) {
                if (uiState.otpSent) {
                    // phone is carried through the nav argument — we read it back from the VM
                    // (stored in the transient otpPhone field below)
                }
            }

            PhoneEntryScreen(
                isLoading = uiState.isLoading,
                error = uiState.error,
                onSendOtp = { phone ->
                    authViewModel.clearError()
                    authViewModel.sendOtp(phone)
                    // Navigate immediately; OTP screen shows loading via shared state
                    navController.navigate(Screen.OtpVerify.createRoute(phone))
                },
            )
        }

        composable(
            route = Screen.OtpVerify.route,
            arguments = listOf(navArgument("phone") { type = NavType.StringType }),
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""

            // Navigate forward once authenticated
            LaunchedEffect(uiState.isAuthenticated) {
                if (uiState.isAuthenticated) {
                    if (uiState.hasProfile) {
                        // Existing profile — go straight to the appropriate home
                        val destination = when (uiState.userRole) {
                            UserRole.CUSTOMER -> Screen.CustomerHome.route
                            UserRole.WORKER -> Screen.WorkerDashboard.route
                            null -> Screen.RoleSelect.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.RoleSelect.route) {
                            popUpTo(Screen.PhoneEntry.route) { inclusive = true }
                        }
                    }
                }
            }

            OtpVerifyScreen(
                phone = phone,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onVerifyOtp = { otp ->
                    authViewModel.clearError()
                    authViewModel.verifyOtp(phone, otp)
                },
            )
        }

        composable(Screen.RoleSelect.route) {
            RoleSelectScreen(
                onRoleSelected = { role ->
                    authViewModel.selectRole(role)
                    navController.navigate(Screen.BasicInfo.route)
                },
            )
        }

        composable(Screen.BasicInfo.route) {
            // After profile is created navigate to the correct home
            LaunchedEffect(uiState.hasProfile) {
                if (uiState.hasProfile) {
                    val destination = when (uiState.userRole) {
                        UserRole.CUSTOMER -> Screen.CustomerHome.route
                        UserRole.WORKER -> Screen.ProfileSetup.route
                        null -> Screen.CustomerHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }

            // currentUserId is needed to build the Profile object; the repository
            // will fill it server-side, so we use an empty placeholder here.
            BasicInfoScreen(
                isLoading = uiState.isLoading,
                error = uiState.error,
                onSubmit = { name, city, area ->
                    val role = uiState.userRole ?: UserRole.CUSTOMER
                    authViewModel.createProfile(
                        Profile(
                            id = "",        // server assigns via auth.uid()
                            fullName = name,
                            phone = "",     // server reads from auth session
                            role = role,
                            city = city,
                            area = area,
                        ),
                    )
                },
            )
        }

        // ── Customer ──────────────────────────────────────────────────────────

        composable(Screen.CustomerHome.route) {
            PlaceholderScreen("Customer Home")
        }
        composable(Screen.MyBookings.route) {
            PlaceholderScreen("My Bookings")
        }
        composable(Screen.PostJob.route) {
            PlaceholderScreen("Post Job")
        }

        // ── Worker ────────────────────────────────────────────────────────────

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

        // ── Shared ────────────────────────────────────────────────────────────

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
