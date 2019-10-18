package token.service

import com.template.flows.issuer.SelfIssueTokenFlow
import com.template.flows.issuer.SendTokenFlow
import com.template.flows.issuer.VerifyOrderFlow
import com.template.states.OrderState
import net.corda.core.messaging.startFlow
import org.springframework.stereotype.Service
import token.common.appexceptions.NotFoundException
import token.dto.order.SelfIssueTokenFlowDTO
import token.dto.order.SendTokenFlowDTO
import token.dto.order.VerifyOrderFlowDTO
import token.dto.order.mapToOrderDTO
import token.service.`interface`.IIssuerService
import token.webserver.NodeRPCConnection

@Service
class IssuerService(private val rpc : NodeRPCConnection) : IIssuerService
{
    override fun getAllOrder(): Any {
        val orderStateRef = rpc.proxy.vaultQuery(OrderState::class.java).states
        return orderStateRef.map { mapToOrderDTO(it.state.data) }
    }

    override fun getOrderById(orderId: String) : Any{
        val orderStateRef = rpc.proxy.vaultQuery(OrderState::class.java).states
        val orderState = orderStateRef.find { it.state.data.linearId.toString() == orderId } ?: throw NotFoundException("User not found")
        return mapToOrderDTO(orderState.state.data)
    }

    override fun verifyOrder(verifyOrder : VerifyOrderFlowDTO) : Any {

        return rpc.proxy.startFlow(::VerifyOrderFlow, verifyOrder.orderId)
    }

    override fun selfIssueToken(SelfIssueToken: SelfIssueTokenFlowDTO) : Any {
        return rpc.proxy.startFlow(::SelfIssueTokenFlow, SelfIssueToken.amount)
}

    override fun sendToken(SendToken : SendTokenFlowDTO) : Any{
        return rpc.proxy.startFlow(::SendTokenFlow, SendToken.orderId)
    }

}
