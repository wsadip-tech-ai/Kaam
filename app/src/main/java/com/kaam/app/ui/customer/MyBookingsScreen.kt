package com.kaam.app.ui.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.data.model.Booking
import com.kaam.app.ui.components.BookingStatusChip
import com.kaam.app.ui.components.EmptyState
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel,
    onBookingClick: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Active", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Past", modifier = Modifier.padding(16.dp))
            }
        }

        val bookings = if (selectedTab == 0) state.activeBookings else state.pastBookings

        if (bookings.isEmpty()) {
            EmptyState(if (selectedTab == 0) "No active bookings" else "No past bookings")
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(bookings, key = { it.id!! }) { booking ->
                    BookingListItem(booking = booking, onClick = { onBookingClick(booking.id!!) })
                }
            }
        }
    }
}

@Composable
private fun BookingListItem(booking: Booking, onClick: () -> Unit) {
    KaamCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = booking.service.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                BookingStatusChip(booking.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${booking.date} • ${booking.timeSlot}", style = MaterialTheme.typography.bodyMedium)
            Text(text = booking.address, style = MaterialTheme.typography.bodySmall)
        }
    }
}
