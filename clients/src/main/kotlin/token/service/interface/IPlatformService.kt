package token.service.`interface`

import token.dto.platform.OrderDTO
import token.dto.platform.OrderFlowDTO
import token.dto.platform.ReserveOrderDTO
import token.dto.platform.TransferReserveOrderFlowDTO

interface IPlatformService {
    fun getAllReserveOrders(): Any
    fun getReserveOrder(linearId: String): Any
    fun getAllOrders(): Any
    fun getOrder(linearId: String): Any
    fun addOrder(request: OrderFlowDTO): OrderDTO
    fun transferToken(request: TransferReserveOrderFlowDTO): ReserveOrderDTO
}