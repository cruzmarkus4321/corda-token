package token.service.`interface`

import token.dto.order.OrderDTO
import token.dto.order.OrderFlowDTO
import token.dto.reserveorder.ReserveOrderDTO
import token.dto.reserveorder.ReserveOrderFlowDTO
import token.dto.reserveorder.TransferReserveOrderFlowDTO

interface IReserveOrderService: IService
{
    fun addReserveOrder(request: ReserveOrderFlowDTO): ReserveOrderDTO
    fun transferToken(linearId: String): ReserveOrderDTO
}