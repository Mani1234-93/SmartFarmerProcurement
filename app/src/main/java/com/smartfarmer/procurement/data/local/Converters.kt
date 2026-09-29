package com.smartfarmer.procurement.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartfarmer.procurement.domain.models.*

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.FARMER)

    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus = runCatching { BookingStatus.valueOf(value) }.getOrDefault(BookingStatus.CONFIRMED)

    @TypeConverter
    fun fromQueueStatus(status: QueueStatus): String = status.name

    @TypeConverter
    fun toQueueStatus(value: String): QueueStatus = runCatching { QueueStatus.valueOf(value) }.getOrDefault(QueueStatus.WAITING)

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String = status.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = runCatching { PaymentStatus.valueOf(value) }.getOrDefault(PaymentStatus.PENDING)

    @TypeConverter
    fun fromCropGrade(grade: CropGrade): String = grade.name

    @TypeConverter
    fun toCropGrade(value: String): CropGrade = runCatching { CropGrade.valueOf(value) }.getOrDefault(CropGrade.GRADE_A)

    @TypeConverter
    fun fromStringList(list: List<String>?): String = gson.toJson(list ?: emptyList<String>())

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return runCatching { gson.fromJson<List<String>>(value, type) }.getOrDefault(emptyList())
    }
}
