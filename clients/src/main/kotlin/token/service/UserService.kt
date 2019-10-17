package token.service

import com.template.states.UserState
import org.springframework.stereotype.Service
import token.common.appexceptions.NotFoundException
import token.dto.user.mapToUserDTO
import token.service.`interface`.IUserService
import token.webserver.NodeRPCConnection

@Service
class UserService (private val rpc: NodeRPCConnection): IUserService
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

    // TODO - service of register user flow

    // TODO - get all / get orders

    // TODO - service of reserve order flow
}