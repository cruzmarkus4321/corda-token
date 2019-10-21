package token.service.`interface`

import token.dto.order.SelfIssueTokenFlowDTO
import token.dto.order.SendTokenFlowDTO
import token.dto.order.VerifyOrderFlowDTO

interface IIssuerService {
    fun getAllOrder() : Any
    fun getOrderById(orderId : String) : Any
    fun verifyOrder(request : VerifyOrderFlowDTO) : Any
    fun selfIssueToken(request : SelfIssueTokenFlowDTO) : Any
    fun sendToken(request : SendTokenFlowDTO) : Any
}