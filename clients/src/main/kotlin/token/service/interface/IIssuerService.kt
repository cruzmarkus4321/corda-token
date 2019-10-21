package token.service.`interface`

import token.dto.platform.SelfIssueTokenFlowDTO
import token.dto.platform.SendTokenFlowDTO
import token.dto.platform.VerifyOrderFlowDTO

interface IIssuerService {
    fun getAllOrder() : Any
    fun getOrderById(orderId : String) : Any
    fun verifyOrder(request : VerifyOrderFlowDTO) : Any
    fun selfIssueToken(request : SelfIssueTokenFlowDTO) : Any
    fun sendToken(request : SendTokenFlowDTO) : Any
}