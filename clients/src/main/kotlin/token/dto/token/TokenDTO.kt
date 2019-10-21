package token.dto.token

import com.r3.corda.lib.tokens.contracts.states.FungibleToken


class TokenDTO(
    val tokenType : String
)

fun mapToTokenDTO(token : FungibleToken) : TokenDTO
{
    return TokenDTO(
        tokenType = token.toString()
    )
}