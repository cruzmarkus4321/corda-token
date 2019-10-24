package com.template.flows.issuer

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.holderString
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.heldTokenAmountCriteria
import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountCriteria
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import javax.swing.JOptionPane

@StartableByRPC
class SelfIssueTokenFlow(private val amount: Double): FlowFunctions()
{
    private var tokenIdentifier : String = ""

    @Suspendable
    override fun call() : SignedTransaction {

        tokenIdentifier = when (ourIdentity.name.organisation) {
            "IssuerPHP" -> "PHP"
            "IssuerUSD" -> "USD"
            else -> throw IllegalStateException("Only the Issuers can issue tokens.")
        }

        val existingToken = getExistingToken()
        val newToken = newToken()

        return when {

            existingToken.amount.toDecimal().toDouble() == 0.toDouble()  -> subFlow(IssueTokensFlow(newToken(), listOf()))
            existingToken.amount.toDecimal().toDouble() > 0.toDouble() -> {
                subFlow(IssueTokensFlow(newToken, listOf()))
                subFlow(RedeemFungibleTokens(
                        amount = existingToken.amount.toDecimal() of existingToken.tokenType,
                        issuer = existingToken.issuer
                ))
            }
            else -> throw java.lang.IllegalStateException("")
        }
    }

    private fun getExistingToken() : FungibleToken
    {
        val queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
        val fungibleTokenRef = serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states.singleOrNull()
        return fungibleTokenRef?.state?.data ?: 0 of TokenType(tokenIdentifier) issuedBy ourIdentity heldBy ourIdentity
    }

    private fun newToken() : FungibleToken
    {
        val existingToken = getExistingToken()
        val fungibleToken = amount of TokenType(tokenIdentifier) issuedBy ourIdentity heldBy ourIdentity
        val newAmount = fungibleToken.amount.plus(existingToken.amount)
        return newAmount.toDecimal() of TokenType(tokenIdentifier) issuedBy ourIdentity heldBy ourIdentity
    }
}
