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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(linearId: String): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}