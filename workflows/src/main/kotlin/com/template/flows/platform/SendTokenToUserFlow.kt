package com.template.flows.platform

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.contracts.HistoryContract
import com.template.contracts.UserContract
import com.template.functions.FlowFunctions
import com.template.states.UserState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class SendTokenToUserFlow(private val amount : Double,
                          private val currency : String,
                          private val senderUserId : String,
                          private val receiverUserId : String) : FlowFunctions() {

    @Suspendable
    override fun call(): SignedTransaction {


        return subFlow(FinalityFlow(verifyAndSign(transaction()), listOf()))
    }

    private fun receiverOutputState() : UserState
    {
        val userState = getUserStateByLinearId(receiverUserId).state.data
        val amountToken = amount of TokenType(currency, 2)
        val wallet = userState.wallet
        val newWalletAmount : MutableList<Amount<TokenType>> = mutableListOf()

        wallet.forEach {
            when(it.token.tokenIdentifier) {
                amountToken.token.tokenIdentifier -> {
                    newWalletAmount.add(it.plus(amountToken))
                }
                else -> {
                    newWalletAmount.add(it)
                }
            }
        }
        return userState.copy(wallet = newWalletAmount)
    }


    private fun transaction() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(UserContract.Commands.Receive(), ourIdentity.owningKey)
        this.addInputState(getUserStateByLinearId(receiverUserId))
        this.addOutputState(receiverOutputState(), UserContract.id)
        this.addCommand(cmd)
    }

}