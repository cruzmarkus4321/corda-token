package com.template.flows.user

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.OrderContract
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
import com.template.states.ReserveOrderState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@StartableByRPC
class ReserveOrderFlow(private val userId: String,
                       private val amount: Double,
                       private val currency: String) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        return subFlow(FinalityFlow(verifyAndSign(transaction()), listOf()))
    }

    private fun outputState(): ReserveOrderState
    {
        return ReserveOrderState (
                userId = userId,
                amount = amount,
                currency = currency,
                orderedAt = Instant.now(),
                transferredAt = null,
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity)
        )
    }

    private fun transaction() : TransactionBuilder
    {
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(ReserveOrderContract.Commands.Reserve(), ourIdentity.owningKey)
        builder.addCommand(cmd)
        builder.addOutputState(outputState())
        return builder
    }
}
