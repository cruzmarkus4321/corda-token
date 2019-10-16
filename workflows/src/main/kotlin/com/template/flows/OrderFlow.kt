package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.OrderContract
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
import com.template.states.OrderState
import com.template.states.ReserveOrderState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class OrderFlow(private val amount : Double,
                private val currency: String) : FlowFunctions()
{

    @Suspendable
    override fun call(): SignedTransaction
    {
        val partialTx = verifyAndSign(transaction())
        val otherPartySession = initiateFlow(stringToParty("IssuerPHP"))
        val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))

        return subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
    }

    private fun outputState() : OrderState
    {
        return OrderState(
                amount = amount,
                currency = currency,
                status = Status.PENDING.name,
                orderedAt = Instant.now(),
                verifiedAt = null,
                transferredAt = null,
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity, stringToParty("IssuerPHP"))
        )
    }

    private fun transaction() : TransactionBuilder
    {
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(OrderContract.Commands.Order(), outputState().participants.map { it.owningKey } )
        builder.addOutputState(outputState())
        builder.addCommand(cmd)

        return builder
    }
}

@InitiatedBy(OrderFlow::class)
class OrderFlowResponder(private val flowSession : FlowSession) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction {
        subFlow(object : SignTransactionFlow(flowSession)
        {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
            }
        })

        return subFlow(ReceiveFinalityFlow(flowSession))
    }
}