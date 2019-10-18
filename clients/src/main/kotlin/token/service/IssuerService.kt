package token.service

import com.template.flows.issuer.SelfIssueTokenFlow
import com.template.flows.issuer.SendTokenFlow
import com.template.flows.issuer.VerifyOrderFlow
import com.template.states.OrderState
import net.corda.core.messaging.startFlow
import org.springframework.stereotype.Service
import token.common.appexceptions.NotFoundException
import token.dto.order.*
import token.service.`interface`.IIssuerService
import token.webserver.NodeRPCConnection
import token.webserver.utilities.FlowHandlerCompletion

@Service
class IssuerService(private val rpc : NodeRPCConnection, private val fhc : FlowHandlerCompletion) : IIssuerService
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

    override fun verifyOrder(request : VerifyOrderFlowDTO) : OrderDTO {
        val flowReturn = rpc.proxy.startFlowDynamic(
                VerifyOrderFlow::class.java,
                request.orderId
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as OrderState
        return mapToOrderDTO(flowResult)
    }

    override fun selfIssueToken(request: SelfIssueTokenFlowDTO) : OrderDTO {
        val flowReturn = rpc.proxy.startFlowDynamic(
                SelfIssueTokenFlow::class.java,
                request.amount
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as OrderState
        return mapToOrderDTO(flowResult)
}

    override fun sendToken(request : SendTokenFlowDTO) : OrderDTO{
        val flowReturn = rpc.proxy.startFlowDynamic(
                SelfIssueTokenFlow::class.java,
                request.orderId
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as OrderState
        return mapToOrderDTO(flowResult)
    }

}
