package com.template.flows.platform

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.contracts.HistoryContract
import com.template.functions.FlowFunctions
import com.template.states.HistoryState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@InitiatingFlow
class RecordHistoryFlow(private val reserveOrderId: String): FlowFunctions(){
    private var tokenIssuer = ""

    @Suspendable
    override fun call(): SignedTransaction {
        tokenIssuer = when (getReserveOrderStateByLinearId(reserveOrderId).state.data.currency) {
            "PHP" -> "IssuerPHP"
            "USD" -> "IssuerUSD"
            else -> throw IllegalStateException("Unknown Issuer")
        }

        val partialTx = verifyAndSign(transaction())
        val otherPartySession = initiateFlow(stringToParty(tokenIssuer))
        val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))

        return subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
    }

    private fun historyState(): HistoryState
    {
        return HistoryState(
                amount = getReserveOrderStateByLinearId(reserveOrderId).state.data.amount,
                currency = getReserveOrderStateByLinearId(reserveOrderId).state.data.currency,
                userId = getReserveOrderStateByLinearId(reserveOrderId).state.data.userId,
                transferredAt = Instant.now(),
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity, stringToParty(tokenIssuer))
            )
    }

    private fun transaction() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(HistoryContract.Commands.Save(), historyState().participants.map { it.owningKey })
        this.addOutputState(historyState(), HistoryContract.id)
        this.addCommand(cmd)
    }
}

@InitiatedBy(RecordHistoryFlow::class)
class RecordHistoryFlowResponder(private val flowSession: FlowSession) : FlowFunctions()
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