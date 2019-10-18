package token.service.`interface`

import token.dto.reserveorder.ReserveOrderDTO
import token.dto.reserveorder.ReserveOrderFlowDTO

interface IReserveOrderService: IService
{
    fun addReserveOrder(request: ReserveOrderFlowDTO): ReserveOrderDTO
}