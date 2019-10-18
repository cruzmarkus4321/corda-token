package token.dto.order

import com.fasterxml.jackson.annotation.JsonCreator
import com.template.states.OrderState

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