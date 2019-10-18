package token.service.`interface`

import token.dto.order.OrderDTO
import token.dto.order.OrderFlowDTO

interface IOrderService: IService {
    fun addOrder(request: OrderFlowDTO): OrderDTO
}