package com.template.flows.user

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.heldBy
import com.r3.corda.lib.tokens.workflows.utilities.heldTokenAmountCriteria
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.transactions.SignedTransaction
import java.lang.IllegalArgumentException
import java.util.*

class UpdatePlatformTokenExchangeFlow(private val amount: Double,
                                      private val currency: String,
                                      private val convertedAmount: Double): FlowFunctions() {
    @Suspendable
    override fun call(): SignedTransaction {
        val otherCurrency = when (currency) {
            "PHP" -> "USD"
            "USD" -> "PHP"
            else -> throw IllegalArgumentException("Unknown currency.")
        }

        subFlow(
                RedeemFungibleTokens(
                        amount = convertedAmount of TokenType(otherCurrency),
                        issuer = ourIdentity,
                        observers = listOf(),
                        queryCriteria = heldTokenAmountCriteria(TokenType(otherCurrency), holder = ourIdentity)
                )
        )

        val newToken = amount of TokenType(currency) issuedBy ourIdentity heldBy ourIdentity

        return subFlow(IssueTokensFlow(newToken, listOf()))
    }
}