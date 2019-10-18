package token.service

import com.template.flows.user.ReserveOrderFlow
import com.template.states.ReserveOrderState
import com.template.states.UserState
import net.corda.core.messaging.startFlow
import org.springframework.stereotype.Service
import token.common.appexceptions.NotFoundException
import token.dto.reserveorder.ReserveOrderDTO
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

    override fun addReserveOrder(reserveOrderDTO: ReserveOrderDTO): Any {
        return rpc.proxy.startFlow(::ReserveOrderFlow, reserveOrderDTO.userId, reserveOrderDTO.amount, reserveOrderDTO.currency)
    }


}