package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.template.contracts.OrderContract
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
import com.template.states.OrderState
import com.template.states.ReserveOrderState
import com.template.types.TokenType
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import org.hibernate.criterion.Order
import java.lang.IllegalStateException
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class VerifyOrderFlow(private val orderId: String) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction
    {

        if(verifyBit())
        {
            val partialTx = verifyAndSign(transaction())
            val otherPartySession = initiateFlow(stringToParty("Platform"))
            val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))

            return subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
        }
        else
        {
            throw IllegalStateException("Order has been already verified!")
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

    private fun orderStateRef() : StateAndRef<OrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(orderId)))
        return serviceHub.vaultService.queryBy<OrderState>(queryCriteria).states.single()
    }

    private fun verifyBit() : Boolean
    {
        if(orderStateRef().state.data.verifiedAt != null)
        {
            return true
        }
        return false
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