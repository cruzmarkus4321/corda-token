package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.template.contracts.UserContract
import com.template.functions.FlowFunctions
import com.template.states.ReserveOrderState
import com.template.states.UserState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

class TransferTokenToWalletFlow(private val reserveOrderId: String): FlowFunctions(){
    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(FinalityFlow(verifyAndSign(transaction()), listOf()))
    }
    private fun reserveOrderStateRef() : StateAndRef<ReserveOrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(reserveOrderId)))
        return serviceHub.vaultService.queryBy<ReserveOrderState>(queryCriteria).states.single()
    }
    private fun inputState() : StateAndRef<UserState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(reserveOrderStateRef().state.data.userId)))
        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
    }
    private fun outputState() : UserState
    {
        val amountInOrder = reserveOrderStateRef().state.data.amount.toInt()
        val currencyInOrder = reserveOrderStateRef().state.data.currency
        val amountOrder : Amount<TokenType> = amountInOrder of TokenType(currencyInOrder, 2)
        val wallet = inputState().state.data.wallet
        val newWalletAmount : MutableList<Amount<TokenType>> = mutableListOf()
        wallet.forEach {
            when(it.token.tokenIdentifier){
                amountOrder.token.tokenIdentifier -> {
                    newWalletAmount.add(it.plus(amountOrder))
                }
                else -> {
                    newWalletAmount.add(it)
                }
            }
        }
        return inputState().state.data.copy(wallet = newWalletAmount)
    }
    private fun transaction() : TransactionBuilder
    {
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(UserContract.Commands.Receive(), inputState().state.data.participants.map { it.owningKey })
        builder.addInputState(inputState())
                .addCommand(cmd)
                .addOutputState(outputState())
        return builder
    }
}