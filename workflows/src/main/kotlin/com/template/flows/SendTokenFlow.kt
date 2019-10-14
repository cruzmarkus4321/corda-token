package com.template.flows

import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.move.MoveFungibleTokensFlow
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountWithIssuerCriteria
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.flows.FlowSession
import net.corda.core.transactions.SignedTransaction

class SendTokenFlow: FlowFunctions() {
    override fun call(): SignedTransaction {
        val holderSession = initiateFlow(ourIdentity)
        val otherHolderSession = initiateFlow(stringToParty("Platform"))

        return subFlow(MoveFungibleTokensFlow(
                partyAndAmount = PartyAndAmount(stringToParty("Platform"), 5 of TokenType("PHP")),
                queryCriteria = tokenAmountWithIssuerCriteria(TokenType("PHP"), ourIdentity),
                participantSessions = listOf(holderSession, otherHolderSession),
                observerSessions = emptyList<FlowSession>()
        ))
    }
}