package token.dto.token

import com.r3.corda.lib.tokens.contracts.states.FungibleToken


class TokenDTO(
    val tokenType : String,
    val holder : String,
    val issuer : String
)

fun mapToTokenDTO(token : FungibleToken) : TokenDTO
{
    return TokenDTO(
        tokenType = token.toString(),
        holder = token.holder.toString(),
        issuer = token.issuer.toString()
    )
}