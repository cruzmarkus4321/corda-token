package com.template.flows.issuer

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.OrderContract
import com.template.functions.FlowFunctions
import com.template.states.OrderState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class VerifyOrderFlow(private val orderId: String) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        if(getStatus() == "PENDING")
        {
            val partialTx = verifyAndSign(transaction())
            val otherPartySession = initiateFlow(stringToParty("Platform"))
            val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))

            return subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
        }
        else
        {
            throw FlowException("Order has been already ${getStatus()}!")
        }
    }

    private fun outputState() : OrderState
    {
        val reserveOrder = getOrderByLinearId(orderId).state.data
        return reserveOrder.copy(
                status = Status.VERIFIED.name,
                verifiedAt = Instant.now()
        )
    }

    private fun transaction() : TransactionBuilder
    {
        val reserveOrder = getOrderByLinearId(orderId).state.data
        val reserveOrderRef = getOrderByLinearId(orderId)
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(OrderContract.Commands.Verify(), reserveOrder.participants.map { it.owningKey })
        builder.addInputState(reserveOrderRef)
        builder.addCommand(cmd)
        builder.addOutputState(outputState())
        return builder
    }

    private fun getStatus() : String
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(orderId)))
        val orderStateRef = serviceHub.vaultService.queryBy<OrderState>(queryCriteria).states.single()
        return orderStateRef.state.data.status
    }
}

@InitiatedBy(VerifyOrderFlow::class)
class VerifyOrderFlowResponder(private val flowSession: FlowSession) : FlowFunctions()
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