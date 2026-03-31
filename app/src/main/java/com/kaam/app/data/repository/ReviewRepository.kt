package com.kaam.app.data.repository

import com.kaam.app.data.model.Review
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val postgrest: Postgrest,
) {

    suspend fun getWorkerReviews(workerId: String): List<Review> {
        return postgrest.from("reviews")
            .select {
                filter { eq("worker_id", workerId) }
            }
            .decodeList<Review>()
    }

    suspend fun createReview(review: Review): Review {
        return postgrest.from("reviews")
            .insert(review) {
                select()
            }
            .decodeSingle<Review>()
    }

    /**
     * Returns the average rating for a worker by fetching all reviews and computing locally.
     * For large datasets, consider a Postgres RPC/view instead.
     */
    suspend fun getAverageRating(workerId: String): Double? {
        val reviews = getWorkerReviews(workerId)
        if (reviews.isEmpty()) return null
        return reviews.map { it.rating }.average()
    }

    suspend fun getReviewByBooking(bookingId: String): Review? {
        return postgrest.from("reviews")
            .select {
                filter { eq("booking_id", bookingId) }
            }
            .decodeSingleOrNull<Review>()
    }

    suspend fun deleteReview(reviewId: String) {
        postgrest.from("reviews")
            .delete {
                filter { eq("id", reviewId) }
            }
    }
}
