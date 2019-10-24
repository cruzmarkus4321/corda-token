package com.template.flows.user

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.contracts.ExchangeContract
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
class ExchangeTokenFlow(private val userId: String,
                        private val amount: Double,
                        private val currency: String) : FlowFunctions() {
    private var convertedAmount: Double = 0.0

    @Suspendable
    override fun call(): SignedTransaction {
        when (currency) {
            "PHP" -> convertedAmount = amount / getPHPRate()
            "USD" -> convertedAmount = amount * getPHPRate()
        }

        subFlow(UpdatePlatformTokenExchangeFlow(amount, currency, convertedAmount))

        return subFlow(FinalityFlow(verifyAndSign(transaction()), listOf()))
    }

    private fun outUserState(): UserState {
        val wallet = getUserStateByLinearId(userId).state.data.wallet
        val newWalletAmount: MutableList<Amount<TokenType>> = mutableListOf()

        wallet.forEach {
            when (it.token.tokenIdentifier) {
                currency -> {
                    val exchangeAmount = amount of TokenType(it.token.tokenIdentifier, 2)
                    newWalletAmount.add(it.minus(exchangeAmount))
                }
                getOtherCurrency(currency) -> {
                    val convertedTokenAmount = convertedAmount of TokenType(it.token.tokenIdentifier, 2)
                    newWalletAmount.add(it.plus(convertedTokenAmount))
                }
                else -> {
                    newWalletAmount.add(it)
                }
            }
        }

        return getUserStateByLinearId(userId).state.data.copy(wallet = newWalletAmount)
    }

    private fun transaction() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(ExchangeContract.Commands.Exchange(), ourIdentity.owningKey)
        this.addInputState(getUserStateByLinearId(userId))
        this.addCommand(cmd)
        this.addOutputState(outUserState(), UserContract.id)
    }
}
