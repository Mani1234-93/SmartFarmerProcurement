package com.smartfarmer.procurement.data.local

import androidx.room.*
import com.smartfarmer.procurement.data.models.*
import com.smartfarmer.procurement.domain.models.QueueStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}

@Dao
interface FarmerDao {
    @Query("SELECT * FROM farmers WHERE id = :farmerId")
    suspend fun getFarmerProfile(farmerId: String): FarmerProfile?

    @Query("SELECT * FROM farmers WHERE mobile = :mobile")
    suspend fun getFarmerByMobile(mobile: String): FarmerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmerProfile(profile: FarmerProfile)

    @Query("SELECT COUNT(*) FROM farmers")
    suspend fun getFarmerCount(): Int
}

@Dao
interface CenterDao {
    @Query("SELECT * FROM procurement_centers")
    fun getAllCenters(): Flow<List<ProcurementCenter>>

    @Query("SELECT * FROM procurement_centers WHERE id = :centerId")
    suspend fun getCenterById(centerId: String): ProcurementCenter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCenters(centers: List<ProcurementCenter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCenter(center: ProcurementCenter)
}

@Dao
interface CropDao {
    @Query("SELECT * FROM crops WHERE activeSeason = 1")
    fun getActiveCrops(): Flow<List<Crop>>

    @Query("SELECT * FROM crops WHERE id = :cropId")
    suspend fun getCropById(cropId: String): Crop?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrops(crops: List<Crop>)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getFarmerBookings(farmerId: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: String): Booking?

    @Query("SELECT * FROM bookings WHERE centerId = :centerId AND bookingDate = :date")
    fun getCenterBookingsByDate(centerId: String, date: String): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Update
    suspend fun updateBooking(booking: Booking)

    @Query("SELECT COUNT(*) FROM bookings WHERE bookingDate = :date")
    suspend fun getTodayBookingsCount(date: String): Int
}

@Dao
interface QueueDao {
    @Query("SELECT * FROM queue_items WHERE centerId = :centerId AND date = :date ORDER BY sequenceNumber ASC")
    fun getCenterQueue(centerId: String, date: String): Flow<List<QueueItem>>

    @Query("SELECT * FROM queue_items WHERE bookingId = :bookingId")
    suspend fun getQueueItemByBooking(bookingId: String): QueueItem?

    @Query("SELECT * FROM queue_items WHERE centerId = :centerId AND date = :date AND status = :status ORDER BY sequenceNumber ASC LIMIT 1")
    suspend fun getCurrentServing(centerId: String, date: String, status: QueueStatus = QueueStatus.CALLED): QueueItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItem(queueItem: QueueItem)

    @Update
    suspend fun updateQueueItem(queueItem: QueueItem)

    @Query("DELETE FROM queue_items WHERE id = :id")
    suspend fun deleteQueueItem(id: String)
}

@Dao
interface ProcurementDao {
    @Query("SELECT * FROM procurement_records WHERE farmerId = :farmerId ORDER BY completedAt DESC")
    fun getFarmerProcurements(farmerId: String): Flow<List<ProcurementRecord>>

    @Query("SELECT * FROM procurement_records WHERE id = :id")
    suspend fun getProcurementById(id: String): ProcurementRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcurement(record: ProcurementRecord)

    @Query("SELECT SUM(totalPayableAmount) FROM procurement_records")
    suspend fun getTotalProcurementAmount(): Double?
}

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts WHERE farmerId = :farmerId ORDER BY receiptNumber DESC")
    fun getFarmerReceipts(farmerId: String): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE receiptNumber = :receiptNumber")
    suspend fun getReceipt(receiptNumber: String): Receipt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt)
}
