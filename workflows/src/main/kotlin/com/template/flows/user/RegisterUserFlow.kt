package com.template.flows.user

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.contracts.UserContract
import com.template.functions.FlowFunctions
import com.template.states.UserState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@StartableByRPC
class RegisterUserFlow(private val name: String,
                       private val amount: MutableList<Double>,
                       private val currency: MutableList<String>): FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        if(ourIdentity.name.organisation == "Platform")
            return subFlow(FinalityFlow(verifyAndSign(transaction()), listOf()))
        else throw IllegalStateException("You are not allowed to use this flow!")
    }

    private fun userState(): UserState
    {
        val moneyList: MutableList<Amount<TokenType>> = mutableListOf()
        val moneyMap : MutableMap<String, Double> = mutableMapOf()

        for(i in amount.indices)
        {
            moneyMap[currency[i]] = amount[i]
        }

        moneyMap.forEach { (tokenName, amount) ->
            moneyList.add(amount of TokenType(tokenName, 2))
        }

        return UserState(
                name = name,
                wallet = moneyList,
                registeredDate = Instant.now(),
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity)
        )
    }

    private fun transaction() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(UserContract.Commands.Add(), ourIdentity.owningKey)
        this.addOutputState(userState(), UserContract.id)
        this.addCommand(cmd)
    }
}