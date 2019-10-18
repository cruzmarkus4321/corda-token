package token.service

import com.r3.corda.lib.tokens.contracts.utilities.amount
import com.template.flows.platform.OrderFlow
import com.template.states.OrderState
import javassist.NotFoundException
import org.springframework.stereotype.Service
import token.dto.order.OrderDTO
import token.dto.order.OrderFlowDTO
import token.dto.order.mapToOrderDTO
import token.service.`interface`.IOrderService
import token.service.`interface`.IService
import token.webserver.NodeRPCConnection
import token.webserver.utilities.FlowHandlerCompletion

@Service
class OrderService(private val rpc: NodeRPCConnection, private val fhc: FlowHandlerCompletion): IOrderService{
    override fun getAll(): Any {
        val orderStateRef = rpc.proxy.vaultQuery(OrderState::class.java).states
        return orderStateRef.map { mapToOrderDTO(it.state.data)}
    }

    override fun get(linearId: String): Any {
        val orderStateRef = rpc.proxy.vaultQuery(OrderState::class.java).states
        val orderState = orderStateRef.find { it.state.data.linearId.toString() == linearId } ?: throw NotFoundException("Order not found")
        return mapToOrderDTO(orderState.state.data)
    }

    override fun addOrder(request: OrderFlowDTO): OrderDTO
    {
        val flowReturn = rpc.proxy.startFlowDynamic(
                OrderFlow::class.java,
                request.amount,
                request.currency
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as OrderState
        return mapToOrderDTO(flowResult)
    }
}