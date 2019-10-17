package com.template.flows.issuer

import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction

@StartableByRPC
class SelfIssueTokenFlow(private val amount: Double): FlowFunctions()
{
    override fun call(): SignedTransaction {

        val tokenIdentifier = when (ourIdentity.name.organisation) {
            "IssuerPHP" -> "PHP"
            "IssuerUSD" -> "USD"
            else -> throw IllegalStateException("Only the Issuers can issue tokens.")
        }

        val fungibleToken = amount of TokenType(tokenIdentifier) issuedBy ourIdentity heldBy ourIdentity

        return subFlow(IssueTokensFlow(fungibleToken, listOf()))
    }
}