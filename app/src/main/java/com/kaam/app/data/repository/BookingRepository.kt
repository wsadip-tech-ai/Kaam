package com.kaam.app.data.repository

import com.kaam.app.data.model.Booking
import com.kaam.app.data.model.BookingStatus
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val postgrest: Postgrest,
) {

    suspend fun getBooking(bookingId: String): Booking? {
        return postgrest.from("bookings")
            .select {
                filter { eq("id", bookingId) }
            }
            .decodeSingleOrNull<Booking>()
    }

    suspend fun getCustomerBookings(customerId: String): List<Booking> {
        return postgrest.from("bookings")
            .select {
                filter { eq("customer_id", customerId) }
            }
            .decodeList<Booking>()
    }

    suspend fun getWorkerBookings(workerId: String): List<Booking> {
        return postgrest.from("bookings")
            .select {
                filter { eq("worker_id", workerId) }
            }
            .decodeList<Booking>()
    }

    suspend fun createBooking(booking: Booking): Booking {
        return postgrest.from("bookings")
            .insert(booking) {
                select()
            }
            .decodeSingle<Booking>()
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Booking {
        return postgrest.from("bookings")
            .update({ set("status", status) }) {
                select()
                filter { eq("id", bookingId) }
            }
            .decodeSingle<Booking>()
    }

    suspend fun updateBooking(booking: Booking): Booking {
        return postgrest.from("bookings")
            .update(booking) {
                select()
                filter { eq("id", booking.id!!) }
            }
            .decodeSingle<Booking>()
    }

    suspend fun deleteBooking(bookingId: String) {
        postgrest.from("bookings")
            .delete {
                filter { eq("id", bookingId) }
            }
    }
}
