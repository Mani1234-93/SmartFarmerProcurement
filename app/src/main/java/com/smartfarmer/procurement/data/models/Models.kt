package com.smartfarmer.procurement.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartfarmer.procurement.domain.models.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val mobile: String = "",
    val role: UserRole = UserRole.FARMER,
    val centerId: String? = null,
    val token: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "farmers")
data class FarmerProfile(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val mobile: String = "",
    val farmerCardNo: String = "",
    val village: String = "",
    val district: String = "",
    val state: String = "",
    val preferredCenterId: String = "",
    val bankAccount: String = "",
    val ifscCode: String = "",
    val totalLandAcres: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "procurement_centers")
data class ProcurementCenter(
    @PrimaryKey val id: String = "",
    val code: String = "",
    val name: String = "",
    val address: String = "",
    val district: String = "",
    val state: String = "",
    val contactPhone: String = "",
    val dailyCapacityQuintals: Double = 1000.0,
    val operatingHours: String = "08:00 AM - 05:00 PM",
    val activeDays: List<String> = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@Entity(tableName = "crops")
data class Crop(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val nameHindi: String = "",
    val variety: String = "",
    val mspRatePerQuintal: Double = 0.0, // Minimum Support Price
    val acceptableMoistureMaxPct: Double = 17.0,
    val category: String = "Kharif",
    val activeSeason: Boolean = true
)

data class TimeSlot(
    val slotId: String = "",
    val startTime: String = "", // e.g. "08:00 AM"
    val endTime: String = "",   // e.g. "09:00 AM"
    val totalCapacity: Int = 20,
    val bookedCount: Int = 0
) {
    val availableCount: Int get() = (totalCapacity - bookedCount).coerceAtLeast(0)
    val isAvailable: Boolean get() = availableCount > 0
}

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val id: String = "", // e.g. "BK202608260012"
    val farmerId: String = "",
    val farmerName: String = "",
    val farmerMobile: String = "",
    val centerId: String = "",
    val centerName: String = "",
    val cropId: String = "",
    val cropName: String = "",
    val expectedQuantityQuintals: Double = 0.0,
    val bookingDate: String = "", // "YYYY-MM-DD"
    val timeSlot: String = "",    // "09:00 AM - 10:00 AM"
    val tokenNumber: String = "", // e.g. "A-124"
    val qrCodePayload: String = "",
    val status: BookingStatus = BookingStatus.CONFIRMED,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "queue_items")
data class QueueItem(
    @PrimaryKey val id: String = "",
    val bookingId: String = "",
    val centerId: String = "",
    val farmerId: String = "",
    val farmerName: String = "",
    val tokenNumber: String = "",
    val cropName: String = "",
    val expectedQuantityQuintals: Double = 0.0,
    val timeSlot: String = "",
    val date: String = "",
    val sequenceNumber: Int = 0,
    val status: QueueStatus = QueueStatus.WAITING,
    val checkInTime: Long? = null,
    val callTime: Long? = null,
    val completionTime: Long? = null
)

@Entity(tableName = "procurement_records")
data class ProcurementRecord(
    @PrimaryKey val id: String = "",
    val bookingId: String = "",
    val farmerId: String = "",
    val farmerName: String = "",
    val centerId: String = "",
    val centerName: String = "",
    val cropId: String = "",
    val cropName: String = "",
    val grossWeightQuintals: Double = 0.0,
    val tareWeightQuintals: Double = 0.0,
    val netWeightQuintals: Double = 0.0,
    val moisturePercentage: Double = 14.0,
    val qualityGrade: CropGrade = CropGrade.GRADE_A,
    val ratePerQuintal: Double = 0.0,
    val moistureDeduction: Double = 0.0,
    val totalPayableAmount: Double = 0.0,
    val verifiedByStaffId: String = "",
    val completedAt: Long = System.currentTimeMillis(),
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val transactionRef: String = ""
)

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey val receiptNumber: String = "",
    val procurementRecordId: String = "",
    val bookingId: String = "",
    val tokenNumber: String = "",
    val farmerName: String = "",
    val farmerId: String = "",
    val centerName: String = "",
    val dateFormatted: String = "",
    val cropName: String = "",
    val netQuantityQuintals: Double = 0.0,
    val qualityGrade: String = "",
    val ratePerQuintal: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val transactionRef: String = "",
    val pdfFilePath: String? = null
)

data class NotificationModel(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "QUEUE_UPDATE",
    val targetUserId: String = "",
    val isRead: Boolean = false
)

data class DashboardSummary(
    val totalFarmers: Int = 0,
    val todayBookings: Int = 0,
    val completedToday: Int = 0,
    val waitingInQueue: Int = 0,
    val cancelledToday: Int = 0,
    val totalProcurementAmount: Double = 0.0,
    val totalQuantityProcured: Double = 0.0
)
