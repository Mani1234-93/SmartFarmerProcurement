package com.smartfarmer.procurement.data.repository

import com.smartfarmer.procurement.data.local.AppDatabase
import com.smartfarmer.procurement.data.models.*
import com.smartfarmer.procurement.domain.models.BookingStatus
import com.smartfarmer.procurement.domain.models.QueueStatus
import com.smartfarmer.procurement.domain.usecases.TokenGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FarmerRepository(private val db: AppDatabase) {
    fun getFarmerProfile(farmerId: String): Flow<List<Booking>> {
        return db.bookingDao().getFarmerBookings(farmerId)
    }

    suspend fun getProfile(farmerId: String): FarmerProfile? = withContext(Dispatchers.IO) {
        db.farmerDao().getFarmerProfile(farmerId)
    }

    fun getAllCenters(): Flow<List<ProcurementCenter>> = db.centerDao().getAllCenters()

    fun getActiveCrops(): Flow<List<Crop>> = db.cropDao().getActiveCrops()

    fun getFarmerProcurements(farmerId: String): Flow<List<ProcurementRecord>> =
        db.procurementDao().getFarmerProcurements(farmerId)

    fun getFarmerReceipts(farmerId: String): Flow<List<Receipt>> =
        db.receiptDao().getFarmerReceipts(farmerId)
}

class BookingRepository(private val db: AppDatabase) {

    fun getFarmerBookings(farmerId: String): Flow<List<Booking>> =
        db.bookingDao().getFarmerBookings(farmerId)

    suspend fun getBookingById(bookingId: String): Booking? = withContext(Dispatchers.IO) {
        db.bookingDao().getBookingById(bookingId)
    }

    suspend fun createBooking(
        farmerId: String,
        farmerName: String,
        farmerMobile: String,
        center: ProcurementCenter,
        crop: Crop,
        expectedQuantity: Double,
        bookingDate: String,
        timeSlot: String
    ): Result<Booking> = withContext(Dispatchers.IO) {
        try {
            val existingBookings = db.bookingDao().getCenterBookingsByDate(center.id, bookingDate).firstOrNull() ?: emptyList()
            val sequence = existingBookings.size + 1

            val bookingId = TokenGenerator.generateBookingId(bookingDate, sequence)
            val tokenNumber = TokenGenerator.generateTokenNumber(timeSlot, sequence)

            // Form QR Code payload (compact JSON-like metadata)
            val qrPayload = "{\"id\":\"$bookingId\",\"t\":\"$tokenNumber\",\"f\":\"$farmerName\",\"c\":\"${crop.name}\",\"q\":$expectedQuantity,\"ctr\":\"${center.code}\",\"d\":\"$bookingDate\"}"

            val booking = Booking(
                id = bookingId,
                farmerId = farmerId,
                farmerName = farmerName,
                farmerMobile = farmerMobile,
                centerId = center.id,
                centerName = center.name,
                cropId = crop.id,
                cropName = crop.name,
                expectedQuantityQuintals = expectedQuantity,
                bookingDate = bookingDate,
                timeSlot = timeSlot,
                tokenNumber = tokenNumber,
                qrCodePayload = qrPayload,
                status = BookingStatus.CONFIRMED
            )

            // Insert booking
            db.bookingDao().insertBooking(booking)

            // Also add to Queue
            val queueItem = QueueItem(
                id = "Q_${booking.id}",
                bookingId = booking.id,
                centerId = center.id,
                farmerId = farmerId,
                farmerName = farmerName,
                tokenNumber = tokenNumber,
                cropName = crop.name,
                expectedQuantityQuintals = expectedQuantity,
                timeSlot = timeSlot,
                date = bookingDate,
                sequenceNumber = sequence,
                status = QueueStatus.WAITING
            )
            db.queueDao().insertQueueItem(queueItem)

            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelBooking(bookingId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val booking = db.bookingDao().getBookingById(bookingId) ?: return@withContext Result.failure(Exception("Booking not found"))
            val updated = booking.copy(status = BookingStatus.CANCELLED)
            db.bookingDao().updateBooking(updated)

            val queueItem = db.queueDao().getQueueItemByBooking(bookingId)
            if (queueItem != null) {
                db.queueDao().updateQueueItem(queueItem.copy(status = QueueStatus.CANCELLED))
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class QueueRepository(private val db: AppDatabase) {

    fun getCenterQueue(centerId: String, date: String): Flow<List<QueueItem>> =
        db.queueDao().getCenterQueue(centerId, date)

    suspend fun updateQueueStatus(queueItemId: String, newStatus: QueueStatus) = withContext(Dispatchers.IO) {
        val allQueue = db.queueDao().getCenterQueue("CTR_001", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())).firstOrNull() ?: emptyList()
        val item = allQueue.find { it.id == queueItemId || it.bookingId == queueItemId || it.tokenNumber == queueItemId }
        if (item != null) {
            val updated = item.copy(
                status = newStatus,
                callTime = if (newStatus == QueueStatus.CALLED) System.currentTimeMillis() else item.callTime,
                completionTime = if (newStatus == QueueStatus.COMPLETED) System.currentTimeMillis() else item.completionTime
            )
            db.queueDao().updateQueueItem(updated)
        }
    }
}
