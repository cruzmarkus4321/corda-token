package token.dto.user

import com.fasterxml.jackson.annotation.JsonCreator
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.template.states.UserState
import net.corda.core.contracts.Amount

data class UserDTO(
        val name: String,
        val wallet: String,
        val registeredDate: String,
        val linearId: String
)

data class RegisterUserFlowDTO @JsonCreator constructor(
        val name: String,
        val amount: MutableList<Double>,
        val currency: MutableList<String>
)

fun mapToUserDTO(user: UserState): UserDTO
{
    return UserDTO(
            name = user.name,
            wallet = user.wallet.toString(),
            registeredDate = user.registeredDate.toString(),
            linearId = user.linearId.toString()
    )
}

data class ExchangeTokenFlowDTO @JsonCreator constructor(
        val userId: String,
        val amount: Double,
        val currency: String
)
