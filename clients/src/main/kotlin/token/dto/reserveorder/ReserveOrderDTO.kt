package token.dto.reserveorder

import com.fasterxml.jackson.annotation.JsonCreator
import com.template.states.ReserveOrderState

data class ReserveOrderDTO(
        val userId: String,
        val amount: Double,
        val currency: String,
        val orderedAt: String,
        val transferredAt: String?,
        val linearId: String
)

data class ReserveOrderFlowDTO @JsonCreator constructor(
        val userId: String,
        val amount: Double,
        val currency: String
)

data class TransferReserveOrderFlowDTO(
        val reserveOrderId: String
)

fun mapToReserveOrderDTO(reserveOrder: ReserveOrderState): ReserveOrderDTO
{
    return ReserveOrderDTO(
            userId = reserveOrder.userId,
            amount = reserveOrder.amount,
            currency = reserveOrder.currency,
            orderedAt = reserveOrder.orderedAt.toString(),
            transferredAt = reserveOrder.transferredAt.toString(),
            linearId = reserveOrder.linearId.toString()
    )
}