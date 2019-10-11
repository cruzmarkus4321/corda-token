package com.template.flows

import com.r3.corda.lib.tokens.contracts.states.AbstractToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.template.contracts.UserContract
import com.template.functions.FlowFunctions
import com.template.states.UserState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@StartableByRPC
class RegisterUserFlow(val name: String): FlowFunctions(){
    override fun call(): SignedTransaction {
        return subFlow(FinalityFlow(verifyAndSign(transaction()), listOf()))
    }

    private fun userState(): UserState{

        return UserState(
                name = name,
                wallet = listOf(0 of TokenType("PHP", 2)),
                registeredDate = Instant.now(),
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity)
        )
    }

    private fun transaction(): TransactionBuilder
    {
        val cmd = Command(UserContract.Commands.Add(), ourIdentity.owningKey)
        val builder = TransactionBuilder(getNotaries())
                .addOutputState(userState(), UserContract.id)
                .addCommand(cmd)

        return builder
    }
}