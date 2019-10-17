package com.template.flows.platform

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.heldTokenAmountCriteria
import com.template.functions.FlowFunctions
import com.template.states.ReserveOrderState
import com.template.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction

class UpdatePlatFormTokenFlow(private val reserveOrderId : String) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction {
        return if(input(getReserveOrder().currency).state.data.amount.quantity >= getReserveOrder().amount){
            subFlow(
                    RedeemFungibleTokens(
                            amount = getReserveOrder().amount of output(getReserveOrder().currency).tokenType,
                            issuer = input(getReserveOrder().currency).state.data.issuer,
                            observers = listOf(),
                            queryCriteria = heldTokenAmountCriteria(TokenType(getReserveOrder().currency), holder = ourIdentity)
                    )
            )
        } else {
            throw IllegalArgumentException("Insufficient Platform Funds.")
        }
    }

    private fun input(currency : String) : StateAndRef<FungibleToken>
    {
        val queryCriteria = heldTokenAmountCriteria(TokenType(currency), holder = ourIdentity)
        return serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states.single()
    }

    private fun getReserveOrder() : ReserveOrderState
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(reserveOrderId)))
        return serviceHub.vaultService.queryBy<ReserveOrderState>(queryCriteria).states.single().state.data
    }

    private fun output(currency: String) : FungibleToken {
        return getReserveOrder().amount of TokenType(currency) issuedBy stringToParty("Platform") heldBy ourIdentity
    }
}