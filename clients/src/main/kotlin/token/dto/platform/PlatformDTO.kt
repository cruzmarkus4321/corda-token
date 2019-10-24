package token.dto.platform

import com.fasterxml.jackson.annotation.JsonCreator
import com.template.states.HistoryState
import com.template.states.OrderState
import com.template.states.ReserveOrderState

data class OrderDTO(val amount: Double,
                    val currency: String,
                    val status: String,
                    val orderedAt: String,
                    val verifiedAt: String?,
                    val transferredAt: String?,
                    val linearId: String)

data class OrderFlowDTO @JsonCreator constructor(
        val amount: Double,
        val currency: String
)

data class VerifyOrderFlowDTO @JsonCreator constructor(
        val orderId: String
)

data class SelfIssueTokenFlowDTO @JsonCreator constructor(
        val amount : Double
)

data class SendTokenFlowDTO @JsonCreator constructor(
        val orderId : String
)

fun mapToOrderDTO(order : OrderState) : OrderDTO
{
    return OrderDTO(
            amount = order.amount,
            currency = order.currency,
            status = order.status,
            orderedAt = order.orderedAt.toString(),
            verifiedAt = order.verifiedAt.toString(),
            transferredAt = order.transferredAt.toString(),
            linearId = order.linearId.toString()
    )
}

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

data class TransferReserveOrderFlowDTO @JsonCreator constructor(
        val reserveOrderId: String
)

fun mapToReserveOrderDTO(reserveOrder: ReserveOrderState): ReserveOrderDTO
{
    return ReserveOrderDTO(
            userId = reserveOrder.userId.toString(),
            amount = reserveOrder.amount,
            currency = reserveOrder.currency,
            orderedAt = reserveOrder.orderedAt.toString(),
            transferredAt = reserveOrder.transferredAt.toString(),
            linearId = reserveOrder.linearId.toString()
    )
}

data class HistoryDTO(
        val amount: Double,
        val currency: String,
        val userId: String,
        val transferredAt: String,
        val linearId: String
)

fun mapToHistoryDTO(historyState: HistoryState): HistoryDTO
{
    return HistoryDTO(
            amount = historyState.amount,
            currency = historyState.currency,
            userId = historyState.userId.toString(),
            transferredAt = historyState.transferredAt.toString(),
            linearId = historyState.linearId.toString()
    )
}

data class SelfIssuePlatformTokenFlowDTO @JsonCreator constructor(
        val amount: Double,
        val currency: String
)