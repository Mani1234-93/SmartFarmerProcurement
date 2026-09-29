package com.smartfarmer.procurement.domain.models

enum class UserRole {
    FARMER,
    STAFF,
    ADMIN
}

enum class BookingStatus {
    CONFIRMED,
    CHECKED_IN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW
}

enum class QueueStatus {
    WAITING,
    CALLED,
    VERIFICATION,
    PROCUREMENT,
    COMPLETED,
    NO_SHOW,
    CANCELLED
}

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    PAID,
    FAILED,
    REJECTED
}

enum class CropGrade {
    GRADE_A,
    GRADE_B,
    GRADE_C
}

enum class AppLanguageCode(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी"),
    CHHATTISGARHI("hne", "छत्तीसगढ़ी")
}
