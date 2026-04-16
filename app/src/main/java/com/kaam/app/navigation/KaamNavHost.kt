package com.kaam.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.kaam.app.ui.chat.ChatListScreen
import com.kaam.app.ui.chat.ChatListViewModel
import com.kaam.app.ui.chat.ChatRoomScreen
import com.kaam.app.ui.chat.ChatRoomViewModel
import com.kaam.app.ui.customer.BookingFormScreen
import com.kaam.app.ui.customer.BookingFormViewModel
import com.kaam.app.ui.customer.HomeScreen
import com.kaam.app.ui.customer.HomeViewModel
import com.kaam.app.ui.customer.JobApplicantsScreen
import com.kaam.app.ui.customer.JobApplicantsViewModel
import com.kaam.app.ui.customer.LeaveReviewScreen
import com.kaam.app.ui.customer.LeaveReviewViewModel
import com.kaam.app.ui.customer.MyBookingsScreen
import com.kaam.app.ui.customer.MyBookingsViewModel
import com.kaam.app.ui.customer.PaymentScreen
import com.kaam.app.ui.customer.PaymentViewModel
import com.kaam.app.ui.customer.PostJobScreen
import com.kaam.app.ui.customer.PostJobViewModel
import com.kaam.app.ui.customer.WorkerListScreen
import com.kaam.app.ui.customer.WorkerListViewModel
import com.kaam.app.ui.customer.WorkerProfileScreen
import com.kaam.app.ui.customer.WorkerProfileViewModel
import com.kaam.app.ui.profile.SettingsScreen
import com.kaam.app.ui.profile.SettingsViewModel
import com.kaam.app.ui.worker.BookingDetailScreen
import com.kaam.app.ui.worker.BookingDetailViewModel
import com.kaam.app.ui.worker.DashboardScreen
import com.kaam.app.ui.worker.DashboardViewModel
import com.kaam.app.ui.worker.JobBoardScreen
import com.kaam.app.ui.worker.JobBoardViewModel
import com.kaam.app.ui.worker.MyReviewsScreen
import com.kaam.app.ui.worker.MyReviewsViewModel
import com.kaam.app.ui.worker.ProfileSetupScreen
import com.kaam.app.ui.worker.ProfileSetupViewModel
import com.kaam.app.ui.worker.VerificationStatusScreen
import com.kaam.app.ui.worker.VerificationStatusViewModel

@Composable
fun KaamNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
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
            PhoneEntryScreen(
                isLoading = uiState.isLoading,
                error = uiState.error,
                onSendOtp = { phone ->
                    authViewModel.clearError()
                    authViewModel.sendOtp(phone)
                    navController.navigate(Screen.OtpVerify.createRoute(phone))
                },
            )
        }

        composable(
            route = Screen.OtpVerify.route,
            arguments = listOf(navArgument("phone") { type = NavType.StringType }),
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""

            LaunchedEffect(uiState.isAuthenticated) {
                if (uiState.isAuthenticated) {
                    if (uiState.hasProfile) {
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

            BasicInfoScreen(
                isLoading = uiState.isLoading,
                error = uiState.error,
                onSubmit = { name, city, area ->
                    val role = uiState.userRole ?: UserRole.CUSTOMER
                    authViewModel.createProfile(
                        Profile(
                            id = "",
                            fullName = name,
                            phone = "",
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
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onServiceClick = { service ->
                    navController.navigate(Screen.WorkerList.createRoute(service))
                },
                onPostJob = {
                    navController.navigate(Screen.PostJob.route)
                },
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.Payment.createRoute(bookingId))
                },
            )
        }

        composable(
            route = Screen.WorkerList.route,
            arguments = listOf(navArgument("service") { type = NavType.StringType }),
        ) {
            val viewModel: WorkerListViewModel = hiltViewModel()
            WorkerListScreen(
                viewModel = viewModel,
                onWorkerClick = { workerId ->
                    navController.navigate(Screen.WorkerProfile.createRoute(workerId))
                },
            )
        }

        composable(
            route = Screen.WorkerProfile.route,
            arguments = listOf(navArgument("workerId") { type = NavType.StringType }),
        ) {
            val viewModel: WorkerProfileViewModel = hiltViewModel()
            WorkerProfileScreen(
                viewModel = viewModel,
                onBookNow = { workerId ->
                    navController.navigate(Screen.BookingForm.createRoute(workerId))
                },
                onSendInquiry = { workerId ->
                    // Navigate to chat — conversation will be created on first message
                    navController.navigate(Screen.ChatList.route)
                },
            )
        }

        composable(
            route = Screen.BookingForm.route,
            arguments = listOf(navArgument("workerId") { type = NavType.StringType }),
        ) {
            val viewModel: BookingFormViewModel = hiltViewModel()
            BookingFormScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate(Screen.MyBookings.route) {
                        popUpTo(Screen.CustomerHome.route)
                    }
                },
            )
        }

        composable(Screen.MyBookings.route) {
            val viewModel: MyBookingsViewModel = hiltViewModel()
            MyBookingsScreen(
                viewModel = viewModel,
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.Payment.createRoute(bookingId))
                },
            )
        }

        composable(Screen.PostJob.route) {
            val viewModel: PostJobViewModel = hiltViewModel()
            PostJobScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = Screen.JobApplicants.route,
            arguments = listOf(navArgument("jobRequestId") { type = NavType.StringType }),
        ) {
            val viewModel: JobApplicantsViewModel = hiltViewModel()
            JobApplicantsScreen(viewModel = viewModel)
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            val viewModel: PaymentViewModel = hiltViewModel()
            PaymentScreen(
                viewModel = viewModel,
                onPaid = {
                    navController.navigate(Screen.LeaveReview.createRoute(bookingId)) {
                        popUpTo(Screen.MyBookings.route)
                    }
                },
            )
        }

        composable(
            route = Screen.LeaveReview.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) {
            val viewModel: LeaveReviewViewModel = hiltViewModel()
            LeaveReviewScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate(Screen.CustomerHome.route) {
                        popUpTo(Screen.CustomerHome.route) { inclusive = true }
                    }
                },
            )
        }

        // ── Worker ────────────────────────────────────────────────────────────

        composable(Screen.ProfileSetup.route) {
            val viewModel: ProfileSetupViewModel = hiltViewModel()
            ProfileSetupScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate(Screen.VerificationStatus.route) {
                        popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.VerificationStatus.route) {
            val viewModel: VerificationStatusViewModel = hiltViewModel()
            VerificationStatusScreen(
                viewModel = viewModel,
                onVerified = {
                    navController.navigate(Screen.WorkerDashboard.route) {
                        popUpTo(Screen.VerificationStatus.route) { inclusive = true }
                    }
                },
                onReUpload = {
                    navController.navigate(Screen.ProfileSetup.route)
                },
            )
        }

        composable(Screen.WorkerDashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = viewModel,
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.BookingDetail.createRoute(bookingId))
                },
            )
        }

        composable(Screen.JobBoard.route) {
            val viewModel: JobBoardViewModel = hiltViewModel()
            JobBoardScreen(viewModel = viewModel)
        }

        composable(
            route = Screen.BookingDetail.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) {
            val viewModel: BookingDetailViewModel = hiltViewModel()
            BookingDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.MyReviews.route) {
            val viewModel: MyReviewsViewModel = hiltViewModel()
            MyReviewsScreen(viewModel = viewModel)
        }

        // ── Shared ────────────────────────────────────────────────────────────

        composable(Screen.ChatList.route) {
            val viewModel: ChatListViewModel = hiltViewModel()
            ChatListScreen(
                viewModel = viewModel,
                onConversationClick = { conversationId ->
                    navController.navigate(Screen.ChatRoom.createRoute(conversationId))
                },
            )
        }

        composable(
            route = Screen.ChatRoom.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
        ) {
            val viewModel: ChatRoomViewModel = hiltViewModel()
            ChatRoomScreen(viewModel = viewModel)
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onLoggedOut = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
