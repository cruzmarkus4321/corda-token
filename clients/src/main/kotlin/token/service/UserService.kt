package token.service

import com.template.flows.user.RegisterUserFlow
import com.template.states.UserState
import net.corda.core.messaging.startFlow
import org.springframework.stereotype.Service
import token.common.appexceptions.NotFoundException
import token.dto.user.RegisterUserFlowDTO
import token.dto.user.UserDTO
import token.dto.user.mapToUserDTO
import token.service.`interface`.IUserService
import token.webserver.NodeRPCConnection
import token.webserver.utilities.FlowHandlerCompletion

@Service
class UserService (private val rpc: NodeRPCConnection, private val fhc: FlowHandlerCompletion): IUserService
{
    override fun getAll(): Any
    {
        val userStateRef = rpc.proxy.vaultQuery(UserState::class.java).states
        return userStateRef.map { mapToUserDTO(it.state.data) }
    }

    override fun get(linearId: String): Any
    {
        val userStateRef = rpc.proxy.vaultQuery(UserState::class.java).states
        val userState = userStateRef.find { it.state.data.linearId.toString() == linearId } ?: throw NotFoundException("User not found")
        return mapToUserDTO(userState.state.data)
    }

    override fun registerUser(request: RegisterUserFlowDTO): UserDTO
    {
        val flowReturn = rpc.proxy.startFlowDynamic(
                RegisterUserFlow::class.java,
                request.name,
                request.amount,
                request.currency
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as UserState
        return mapToUserDTO(flowResult)
    }

    // TODO - service of reserve order flow
}