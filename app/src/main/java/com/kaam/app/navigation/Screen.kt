package com.kaam.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Auth
    data object Welcome : Screen("welcome")
    data object PhoneEntry : Screen("phone_entry")
    data object OtpVerify : Screen("otp_verify/{phone}") {
        fun createRoute(phone: String) = "otp_verify/$phone"
    }
    data object RoleSelect : Screen("role_select")
    data object BasicInfo : Screen("basic_info")

    // Customer
    data object CustomerHome : Screen("customer_home")
    data object WorkerList : Screen("worker_list/{service}") {
        fun createRoute(service: String) = "worker_list/$service"
    }
    data object WorkerProfile : Screen("worker_profile/{workerId}") {
        fun createRoute(workerId: String) = "worker_profile/$workerId"
    }
    data object BookingForm : Screen("booking_form/{workerId}") {
        fun createRoute(workerId: String) = "booking_form/$workerId"
    }
    data object PostJob : Screen("post_job")
    data object MyBookings : Screen("my_bookings")
    data object JobApplicants : Screen("job_applicants/{jobRequestId}") {
        fun createRoute(jobRequestId: String) = "job_applicants/$jobRequestId"
    }
    data object Payment : Screen("payment/{bookingId}") {
        fun createRoute(bookingId: String) = "payment/$bookingId"
    }
    data object LeaveReview : Screen("leave_review/{bookingId}") {
        fun createRoute(bookingId: String) = "leave_review/$bookingId"
    }

    // Worker
    data object ProfileSetup : Screen("profile_setup")
    data object VerificationStatus : Screen("verification_status")
    data object WorkerDashboard : Screen("worker_dashboard")
    data object JobBoard : Screen("job_board")
    data object BookingDetail : Screen("booking_detail/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_detail/$bookingId"
    }
    data object MyReviews : Screen("my_reviews")

    // Shared
    data object ChatList : Screen("chat_list")
    data object ChatRoom : Screen("chat_room/{conversationId}") {
        fun createRoute(conversationId: String) = "chat_room/$conversationId"
    }
    data object Settings : Screen("settings")
    data object Notifications : Screen("notifications")
}

enum class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    // Customer tabs
    CustomerHome(Screen.CustomerHome.route, Icons.Default.Home, "Home"),
    CustomerBookings(Screen.MyBookings.route, Icons.AutoMirrored.Filled.ListAlt, "Bookings"),
    CustomerChat(Screen.ChatList.route, Icons.AutoMirrored.Filled.Chat, "Chat"),
    CustomerProfile(Screen.Settings.route, Icons.Default.Person, "Profile"),

    // Worker tabs
    WorkerDashboard(Screen.WorkerDashboard.route, Icons.Default.Dashboard, "Dashboard"),
    WorkerJobs(Screen.JobBoard.route, Icons.Default.WorkOutline, "Jobs"),
    WorkerChat(Screen.ChatList.route, Icons.AutoMirrored.Filled.Chat, "Chat"),
    WorkerProfile(Screen.Settings.route, Icons.Default.Person, "Profile"),
}
