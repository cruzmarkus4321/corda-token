package token.service

import com.template.flows.platform.OrderFlow
import com.template.flows.platform.TransferTokenFlow
import com.template.states.OrderState
import com.template.states.ReserveOrderState
import javassist.NotFoundException
import org.springframework.stereotype.Service
import token.dto.platform.*
import token.service.`interface`.IPlatformService
import token.webserver.NodeRPCConnection
import token.webserver.utilities.FlowHandlerCompletion

@Service
class PlatformService(private val rpc: NodeRPCConnection, private val fhc: FlowHandlerCompletion): IPlatformService
{
    override fun getAllOrders(): Any {
        val orderStateRef = rpc.proxy.vaultQuery(OrderState::class.java).states
        return orderStateRef.map { mapToOrderDTO(it.state.data)}
    }

    override fun getOrder(linearId: String): Any {
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

    override fun getAllReserveOrders(): Any {
        val reserveOrderStateRef = rpc.proxy.vaultQuery(ReserveOrderState::class.java).states
        return reserveOrderStateRef.map { mapToReserveOrderDTO(it.state.data) }
    }

    override fun getReserveOrder(linearId: String): Any {
        val reserveOrderStateRef = rpc.proxy.vaultQuery(ReserveOrderState::class.java).states
        val reserveOrderState = reserveOrderStateRef.find { it.state.data.linearId.toString() == linearId } ?: throw token.common.appexceptions.NotFoundException("Reserve order not found")
        return mapToReserveOrderDTO(reserveOrderState.state.data)
    }

    override fun transferToken(request: TransferReserveOrderFlowDTO): ReserveOrderDTO {
        val flowReturn = rpc.proxy.startFlowDynamic(
                TransferTokenFlow::class.java,
                request.reserveOrderId
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as ReserveOrderState
        return mapToReserveOrderDTO(flowResult)
    }
}