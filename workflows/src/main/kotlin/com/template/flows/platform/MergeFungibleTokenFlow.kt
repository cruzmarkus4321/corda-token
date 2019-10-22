package com.template.flows.platform

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountCriteria
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import javax.swing.JOptionPane

@StartableByRPC
class MergeFungibleTokenFlow(private val tokenIdentifier : String) : FlowFunctions() {

    private var tokenIssuer : String = ""

    @Suspendable
    override fun call(): SignedTransaction {

        tokenIssuer = when(tokenIdentifier) {
            "PHP" -> "IssuerPHP"
            "USD" -> "IssuerUSD"
            else -> throw java.lang.IllegalArgumentException("Cannot recognized Token Identifier.")
        }
        JOptionPane.showMessageDialog(null, tokenIssuer)


        val newToken = newToken()
        val num = newToken.amount.toDecimal().toDouble()

        JOptionPane.showMessageDialog(null, num)
        JOptionPane.showMessageDialog(null, newToken)

        return when {

            num == 0.toDouble()  -> subFlow(IssueTokensFlow(newToken, listOf()))
            num > 0.toDouble() -> {
                getAllExistingToken().asReversed().forEach {
                    subFlow(RedeemFungibleTokens(
                        amount = it.state.data.amount.toDecimal().toDouble() of newToken.tokenType,
                        issuer = newToken.issuer
                    ))
                }
                subFlow(IssueTokensFlow(newToken, listOf()))
            }
            else -> throw IllegalArgumentException("Amount Error!")
        }
    }

    private fun getAllExistingToken() : List<StateAndRef<FungibleToken>>
    {
        val queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
        val fungibleTokenRef = serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states
        return fungibleTokenRef.toList()
    }

    private fun getExistingToken() : FungibleToken
    {
        val queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
        val fungibleTokenRef = serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states.singleOrNull()
        return fungibleTokenRef?.state?.data ?: 0 of TokenType(tokenIdentifier) issuedBy stringToParty(tokenIssuer) heldBy ourIdentity
    }

    private fun newToken() : FungibleToken
    {
        var amount = 0.0
        getAllExistingToken().forEach {
            amount += it.state.data.amount.toDecimal().toDouble()
        }

        return amount of TokenType(tokenIdentifier) issuedBy stringToParty(tokenIssuer) heldBy ourIdentity
    }
}