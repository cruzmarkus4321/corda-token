package com.template.flows

import co.paralleluniverse.fibers.Suspendable
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
import net.corda.core.transactions.SignedTransaction

@InitiatingFlow
class MoveIssuerTokenFlow(private val amount: Double,
                          private val holder: Party,
                          private val otherHolder: Party): FlowFunctions() {

    @Suspendable
    override fun call(): SignedTransaction {
        val holderSession = initiateFlow(holder)
        val otherHolderSession = initiateFlow(otherHolder)

        return subFlow(MoveFungibleTokensFlow(
                partyAndAmount = PartyAndAmount(otherHolder, amount of TokenType("PHP")),
                queryCriteria = heldTokenAmountCriteria(TokenType("PHP"), holder),
                participantSessions = listOf(holderSession, otherHolderSession),
                observerSessions = emptyList<FlowSession>()
        ))
    }
}

@InitiatedBy(MoveIssuerTokenFlow::class)
class MoveIssuerTokenFlowResponder(private val flowSession: FlowSession): FlowLogic<Unit>() {

    @Suspendable
    override fun call(): Unit {
        return subFlow(MoveTokensFlowHandler(flowSession))
    }
}