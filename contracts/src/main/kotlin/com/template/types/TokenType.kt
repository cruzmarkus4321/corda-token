package com.template.types

import com.r3.corda.lib.tokens.contracts.types.TokenType

data class TokenType(
        override val tokenIdentifier: String,
        override val fractionDigits: Int = 2
) : TokenType(tokenIdentifier, fractionDigits) {
    override fun toString(): String = tokenIdentifier
}