package token.service.`interface`

import token.dto.reserveorder.ReserveOrderDTO

interface IReserveOrderService: IService
{
    fun addReserveOrder(reserveOrderDTO: ReserveOrderDTO): ReserveOrderDTO
}