package com.template.flows.issuer

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.move.MoveFungibleTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.move.MoveTokensFlowHandler
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.heldTokenAmountCriteria
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import java.lang.IllegalArgumentException

@InitiatingFlow
class MoveIssuerTokenFlow(private val amount: Double,
                          private val holder: Party,
                          private val otherHolder: Party): FlowFunctions() {

    @Suspendable
    override fun call(): SignedTransaction {
        val holderSession = initiateFlow(holder)
        val otherHolderSession = initiateFlow(otherHolder)

        val tokenIdentifier = when (holder.name.organisation) {
            "IssuerPHP" -> "PHP"
            "IssuerUSD" -> "USD"
            else -> throw IllegalStateException("Only the Issuers can issue tokens.")
        }

        return if(getTokenAmount(tokenIdentifier) >= amount){
            subFlow(MoveFungibleTokensFlow(
                    partyAndAmount = PartyAndAmount(otherHolder, amount of TokenType(tokenIdentifier)),
                    queryCriteria = heldTokenAmountCriteria(TokenType(tokenIdentifier), holder),
                    participantSessions = listOf(holderSession, otherHolderSession),
                    observerSessions = emptyList()
            ))
        } else {
            throw IllegalArgumentException("Insufficient $tokenIdentifier.")
        }
    }

    private fun getTokenAmount(currency: String) : Double
    {
        val queryCriteria = heldTokenAmountCriteria(TokenType(currency), holder = ourIdentity)
        return serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states.single().state.data.amount.quantity.toDouble()
    }
}

@InitiatedBy(MoveIssuerTokenFlow::class)
class MoveIssuerTokenFlowResponder(private val flowSession: FlowSession): FlowLogic<Unit>() {

    @Suspendable
    override fun call(): Unit {
        return subFlow(MoveTokensFlowHandler(flowSession))
    }
}