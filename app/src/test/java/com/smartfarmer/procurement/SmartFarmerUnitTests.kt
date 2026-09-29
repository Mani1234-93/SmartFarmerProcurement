package com.smartfarmer.procurement

import com.smartfarmer.procurement.domain.models.CropGrade
import com.smartfarmer.procurement.domain.models.QueueStatus
import com.smartfarmer.procurement.domain.usecases.*
import com.smartfarmer.procurement.data.models.QueueItem
import org.junit.Assert.*
import org.junit.Test

class SmartFarmerUnitTests {

    @Test
    fun testTokenNumberGeneration_CorrectPrefixAndSequence() {
        val tokenA = TokenGenerator.generateTokenNumber("08:00 AM - 09:00 AM", 1)
        assertEquals("A-101", tokenA)

        val tokenB = TokenGenerator.generateTokenNumber("09:00 AM - 10:00 AM", 24)
        assertEquals("B-124", tokenB)

        val tokenC = TokenGenerator.generateTokenNumber("10:00 AM - 11:00 AM", 5)
        assertEquals("C-105", tokenC)
    }

    @Test
    fun testBookingIdGeneration_StandardFormat() {
        val bookingId = TokenGenerator.generateBookingId("2026-08-26", 12)
        assertEquals("BK202608260012", bookingId)
    }

    @Test
    fun testPricingCalculation_StandardMoistureGradeA() {
        // 50 Quintals at ₹2,300/Q with 14% moisture (within 17% standard)
        val result = PricingCalculator.calculate(
            grossWeight = 52.0,
            tareWeight = 2.0,
            baseMspRate = 2300.0,
            moisturePct = 14.0,
            standardMoistureMax = 17.0,
            grade = CropGrade.GRADE_A
        )

        assertEquals(50.0, result.netWeightQuintals, 0.001)
        assertEquals(2300.0, result.ratePerQuintal, 0.001)
        assertEquals(0.0, result.moistureDeductionAmount, 0.001)
        assertEquals(115000.0, result.totalPayableAmount, 0.01)
    }

    @Test
    fun testPricingCalculation_ExcessMoistureDeduction() {
        // 100 Quintals at ₹2,300/Q with 20% moisture (3% excess above 17% limit -> 3% penalty)
        val result = PricingCalculator.calculate(
            grossWeight = 105.0,
            tareWeight = 5.0,
            baseMspRate = 2300.0,
            moisturePct = 20.0,
            standardMoistureMax = 17.0,
            grade = CropGrade.GRADE_A
        )

        assertEquals(100.0, result.netWeightQuintals, 0.001)
        assertEquals(3.0, result.moistureDeductionRatePct, 0.001)
        // Base amount: 230,000, 3% deduction = 6,900 => Total: 223,100
        assertEquals(6900.0, result.moistureDeductionAmount, 0.01)
        assertEquals(223100.0, result.totalPayableAmount, 0.01)
    }

    @Test
    fun testPricingCalculation_GradeBDiscount() {
        // Grade B has 2% discount on MSP
        val result = PricingCalculator.calculate(
            grossWeight = 10.0,
            tareWeight = 0.0,
            baseMspRate = 2000.0,
            moisturePct = 12.0,
            grade = CropGrade.GRADE_B
        )

        assertEquals(1960.0, result.ratePerQuintal, 0.001)
        assertEquals(19600.0, result.totalPayableAmount, 0.01)
    }

    @Test
    fun testQueueWaitTimeCalculation_CorrectPeopleAheadAndTurn() {
        val queue = listOf(
            QueueItem(id = "1", tokenNumber = "A-101", status = QueueStatus.COMPLETED, sequenceNumber = 1),
            QueueItem(id = "2", tokenNumber = "A-102", status = QueueStatus.CALLED, sequenceNumber = 2),
            QueueItem(id = "3", tokenNumber = "A-103", status = QueueStatus.WAITING, sequenceNumber = 3),
            QueueItem(id = "4", tokenNumber = "A-104", status = QueueStatus.WAITING, sequenceNumber = 4),
            QueueItem(id = "5", tokenNumber = "A-105", status = QueueStatus.WAITING, sequenceNumber = 5)
        )

        // Target farmer has token A-105
        val progress = CalculateWaitTimeUseCase.calculate(queue, "A-105")

        assertEquals("A-102", progress.currentServingToken)
        assertEquals(3, progress.peopleAhead) // A-103, A-104, A-105 distance from A-102
        assertEquals(21, progress.estimatedWaitMinutes) // 3 * 7 min = 21 min
        assertFalse(progress.isYourTurn)

        // Now test when farmer A-102 is called
        val turnProgress = CalculateWaitTimeUseCase.calculate(queue, "A-102")
        assertTrue(turnProgress.isYourTurn)
    }
}
