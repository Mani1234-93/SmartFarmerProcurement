package com.smartfarmer.procurement.domain.usecases

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TokenGenerator {
    /**
     * Generates a human-readable token like "A-101", "B-214"
     * Prefix is determined by the slot index/hour, and number is sequential.
     */
    fun generateTokenNumber(timeSlot: String, sequenceInSlot: Int): String {
        val slotLetter = when {
            timeSlot.contains("08:00") -> "A"
            timeSlot.contains("09:00") -> "B"
            timeSlot.contains("10:00") -> "C"
            timeSlot.contains("11:00") -> "D"
            timeSlot.contains("12:00") -> "E"
            timeSlot.contains("01:00") || timeSlot.contains("13:00") -> "F"
            timeSlot.contains("02:00") || timeSlot.contains("14:00") -> "G"
            timeSlot.contains("03:00") || timeSlot.contains("15:00") -> "H"
            else -> "T"
        }
        val paddedSeq = (100 + sequenceInSlot).toString()
        return "$slotLetter-$paddedSeq"
    }

    /**
     * Generates a formal Booking ID e.g. "BK202608260012"
     */
    fun generateBookingId(date: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()), sequence: Int): String {
        val cleanDate = date.replace("-", "")
        val formattedSeq = String.format(Locale.US, "%04d", sequence)
        return "BK$cleanDate$formattedSeq"
    }

    /**
     * Generates a formal Transaction reference e.g. "TXN78945612"
     */
    fun generateTransactionRef(): String {
        val randomSuffix = (100000..999999).random()
        return "TXN$randomSuffix"
    }
}
