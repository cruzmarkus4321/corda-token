package com.template.flows.platform


import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountCriteria
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction

class MergeFungibleTokenFlow(private val tokenIdentifier: String) : FlowFunctions()
{

    @Suspendable
    override fun call(): SignedTransaction {

        return subFlow(MoveFungibleTokens(
                partyAndAmount = PartyAndAmount(ourIdentity, tokenAmount() of TokenType(tokenIdentifier)),
                queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
        ))
    }

    private fun getAllExistingToken() : List<StateAndRef<FungibleToken>>
    {
        val queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
        val fungibleTokenRef = serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states
        return fungibleTokenRef.toList()
    }

    private fun tokenAmount() : Double
    {
        var amount = 0.toDouble()

        getAllExistingToken().forEach {
            amount += it.state.data.amount.toDecimal().toDouble()
        }
        return if(amount > 0)amount else throw IllegalArgumentException("")
    }
}