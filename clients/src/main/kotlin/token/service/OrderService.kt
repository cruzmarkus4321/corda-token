package token.service

import com.template.states.OrderState
import org.springframework.stereotype.Service
import token.service.`interface`.IOrderService
import token.service.`interface`.IService
import token.webserver.NodeRPCConnection
import token.webserver.utilities.FlowHandlerCompletion

@Service
class OrderService(private val rpc: NodeRPCConnection, private val fhc: FlowHandlerCompletion): IOrderService{
    override fun getAll(): Any {
        val orderStateRef = rpc.proxy.vaultQuery(OrderState::class.java).states
        val
    }

    override fun get(linearId: String): Any {
        val orderStateRef = rpc.proxy.vaultQuery(OrderState::class.java).states
    }

}