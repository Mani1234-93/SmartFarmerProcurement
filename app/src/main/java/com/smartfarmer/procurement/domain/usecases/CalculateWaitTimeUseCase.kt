package com.smartfarmer.procurement.domain.usecases

import com.smartfarmer.procurement.data.models.QueueItem
import com.smartfarmer.procurement.domain.models.QueueStatus
import kotlin.math.max

data class QueueProgress(
    val currentServingToken: String = "--",
    val yourToken: String = "--",
    val peopleAhead: Int = 0,
    val estimatedWaitMinutes: Int = 0,
    val queueStatus: QueueStatus = QueueStatus.WAITING,
    val isYourTurn: Boolean = false
)

object CalculateWaitTimeUseCase {
    private const val AVERAGE_MINUTES_PER_FARMER = 7

    /**
     * Computes queue position and estimated wait time in minutes for a given farmer token.
     */
    fun calculate(
        queueList: List<QueueItem>,
        targetToken: String
    ): QueueProgress {
        if (queueList.isEmpty()) {
            return QueueProgress(yourToken = targetToken)
        }

        val activeList = queueList.filter {
            it.status == QueueStatus.WAITING ||
            it.status == QueueStatus.CALLED ||
            it.status == QueueStatus.VERIFICATION ||
            it.status == QueueStatus.PROCUREMENT
        }.sortedBy { it.sequenceNumber }

        val currentlyServing = activeList.firstOrNull {
            it.status == QueueStatus.CALLED ||
            it.status == QueueStatus.VERIFICATION ||
            it.status == QueueStatus.PROCUREMENT
        } ?: activeList.firstOrNull()

        val targetItem = queueList.find { it.tokenNumber.equals(targetToken, ignoreCase = true) }
        if (targetItem == null) {
            return QueueProgress(
                currentServingToken = currentlyServing?.tokenNumber ?: "--",
                yourToken = targetToken
            )
        }

        val targetIndex = activeList.indexOfFirst { it.tokenNumber == targetToken }
        val servingIndex = if (currentlyServing != null) activeList.indexOf(currentlyServing) else 0

        val peopleAhead = if (targetIndex >= 0 && servingIndex >= 0) {
            max(0, targetIndex - servingIndex)
        } else {
            0
        }

        val waitMinutes = peopleAhead * AVERAGE_MINUTES_PER_FARMER
        val isYourTurn = targetItem.status == QueueStatus.CALLED ||
                targetItem.status == QueueStatus.VERIFICATION ||
                targetItem.status == QueueStatus.PROCUREMENT

        return QueueProgress(
            currentServingToken = currentlyServing?.tokenNumber ?: "--",
            yourToken = targetToken,
            peopleAhead = peopleAhead,
            estimatedWaitMinutes = waitMinutes,
            queueStatus = targetItem.status,
            isYourTurn = isYourTurn
        )
    }
}
