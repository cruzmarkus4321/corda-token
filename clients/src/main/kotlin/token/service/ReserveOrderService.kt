package token.service

import com.template.contracts.ReserveOrderContract
import com.template.flows.user.ReserveOrderFlow
import com.template.states.ReserveOrderState
import com.template.states.UserState
import net.corda.core.messaging.startFlow
import org.springframework.stereotype.Service
import token.common.appexceptions.NotFoundException
import token.dto.reserveorder.ReserveOrderDTO
import token.dto.reserveorder.ReserveOrderFlowDTO
import token.dto.reserveorder.mapToReserveOrderDTO
import token.dto.user.mapToUserDTO
import token.service.`interface`.IReserveOrderService
import token.webserver.NodeRPCConnection
import token.webserver.utilities.FlowHandlerCompletion

@Service
class ReserveOrderService(private val rpc: NodeRPCConnection, private val fhc: FlowHandlerCompletion): IReserveOrderService {
    override fun getAll(): Any {
        val reserveOrderStateRef = rpc.proxy.vaultQuery(ReserveOrderState::class.java).states
        return reserveOrderStateRef.map { mapToReserveOrderDTO(it.state.data) }
    }

    override fun get(linearId: String): Any {
        val reserveOrderStateRef = rpc.proxy.vaultQuery(ReserveOrderState::class.java).states
        val reserveOrderState = reserveOrderStateRef.find { it.state.data.linearId.toString() == linearId } ?: throw NotFoundException("Reserve order not found")
        return mapToReserveOrderDTO(reserveOrderState.state.data)
    }

    override fun addReserveOrder(request: ReserveOrderFlowDTO): ReserveOrderDTO {
        val flowReturn = rpc.proxy.startFlowDynamic(
                ReserveOrderFlow::class.java,
                request.userId,
                request.amount,
                request.currency
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as ReserveOrderState
        return mapToReserveOrderDTO(flowResult)
    }
}