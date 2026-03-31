package com.kaam.app.data.repository

import io.github.jan.supabase.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(
    private val storage: Storage,
) {

    /**
     * Uploads a user avatar to the "avatars" bucket.
     * Returns the public URL of the uploaded file.
     */
    suspend fun uploadAvatar(userId: String, bytes: ByteArray): String {
        val path = "$userId/avatar.jpg"
        storage.from("avatars").upload(path, bytes) {
            upsert = true
        }
        return storage.from("avatars").publicUrl(path)
    }

    /**
     * Uploads a verification document (e.g. citizenship) to the "documents" bucket.
     * Returns the storage path of the uploaded file.
     */
    suspend fun uploadDocument(userId: String, bytes: ByteArray, fileName: String): String {
        val path = "$userId/$fileName"
        storage.from("documents").upload(path, bytes) {
            upsert = true
        }
        return path
    }

    /**
     * Uploads a payment QR code image to the "payment-qr" bucket.
     * Returns the public URL of the uploaded file.
     */
    suspend fun uploadPaymentQr(userId: String, bytes: ByteArray): String {
        val path = "$userId/payment_qr.jpg"
        storage.from("payment-qr").upload(path, bytes) {
            upsert = true
        }
        return storage.from("payment-qr").publicUrl(path)
    }
}
