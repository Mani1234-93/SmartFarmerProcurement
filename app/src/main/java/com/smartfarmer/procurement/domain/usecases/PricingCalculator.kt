package com.smartfarmer.procurement.domain.usecases

import com.smartfarmer.procurement.domain.models.CropGrade
import java.math.BigDecimal
import java.math.RoundingMode

data class ProcurementCalculationResult(
    val grossWeightQuintals: Double,
    val tareWeightQuintals: Double,
    val netWeightQuintals: Double,
    val ratePerQuintal: Double,
    val qualityMultiplier: Double,
    val moistureDeductionRatePct: Double,
    val moistureDeductionAmount: Double,
    val totalPayableAmount: Double
)

object PricingCalculator {
    /**
     * Calculates procurement price with moisture & grade deductions.
     * Standard acceptable moisture is 14%-17%.
     * For moisture > standard, deduction of 1% per 1% excess moisture applies.
     */
    fun calculate(
        grossWeight: Double,
        tareWeight: Double,
        baseMspRate: Double,
        moisturePct: Double,
        standardMoistureMax: Double = 17.0,
        grade: CropGrade = CropGrade.GRADE_A
    ): ProcurementCalculationResult {
        val netWeight = (grossWeight - tareWeight).coerceAtLeast(0.0)

        val gradeMultiplier = when (grade) {
            CropGrade.GRADE_A -> 1.0
            CropGrade.GRADE_B -> 0.98 // 2% discount for Grade B
            CropGrade.GRADE_C -> 0.95 // 5% discount for Grade C
        }

        val effectiveRate = baseMspRate * gradeMultiplier

        val excessMoisturePct = (moisturePct - standardMoistureMax).coerceAtLeast(0.0)
        val moistureDeductionRatePct = excessMoisturePct * 1.0 // 1% deduction per excess %

        val baseAmount = netWeight * effectiveRate
        val moistureDeductionAmount = baseAmount * (moistureDeductionRatePct / 100.0)
        val totalPayable = (baseAmount - moistureDeductionAmount).coerceAtLeast(0.0)

        return ProcurementCalculationResult(
            grossWeightQuintals = round2(grossWeight),
            tareWeightQuintals = round2(tareWeight),
            netWeightQuintals = round2(netWeight),
            ratePerQuintal = round2(effectiveRate),
            qualityMultiplier = gradeMultiplier,
            moistureDeductionRatePct = round2(moistureDeductionRatePct),
            moistureDeductionAmount = round2(moistureDeductionAmount),
            totalPayableAmount = round2(totalPayable)
        )
    }

    private fun round2(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
