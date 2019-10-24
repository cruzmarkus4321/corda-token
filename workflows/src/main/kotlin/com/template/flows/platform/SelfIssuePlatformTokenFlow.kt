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
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction

@StartableByRPC
class SelfIssuePlatformTokenFlow(private val amount: Double,
                                 private val currency: String): FlowFunctions(){
    @Suspendable
    override fun call() : SignedTransaction {
        val fungibleToken = amount of TokenType(currency) issuedBy ourIdentity heldBy ourIdentity
        return subFlow(IssueTokensFlow(fungibleToken, listOf()))
    }
}
