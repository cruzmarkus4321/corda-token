package token.service.`interface`

import token.dto.platform.*
import token.dto.token.TokenDTO

interface IPlatformService {
    fun getAllReserveOrders(): Any
    fun getReserveOrder(linearId: String): Any
    fun getAllOrders(): Any
    fun getOrder(linearId: String): Any
    fun getAllTokens(): Any
    fun selfIssuePlatformToken(request: SelfIssuePlatformTokenFlowDTO): TokenDTO
    fun addOrder(request: OrderFlowDTO): OrderDTO
    fun transferToken(request: TransferReserveOrderFlowDTO): HistoryDTO
}