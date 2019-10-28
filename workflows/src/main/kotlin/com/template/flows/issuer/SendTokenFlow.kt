package com.template.flows.issuer

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.OrderContract
import com.template.flows.platform.MergeFungibleTokenFlow
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
import java.lang.IllegalArgumentException
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class SendTokenFlow(private val orderId : String): FlowFunctions() {

    @Suspendable
    override fun call(): SignedTransaction {
        val partialTx = verifyAndSign(transaction())
        val otherPartySession = initiateFlow(stringToParty("Platform"))
        val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))

        return when (inputState().state.data.status) {
            "PENDING" -> throw IllegalArgumentException("Order must be verified first!")
            "COMPLETED" -> throw IllegalArgumentException("Order already completed!")
            "VERIFIED" -> {

                subFlow(MoveIssuerTokenFlow(inputState().state.data.amount, ourIdentity, stringToParty("Platform")))
                subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
            }
            else -> throw IllegalArgumentException("Order error!")
        }
    }

    private fun inputState() : StateAndRef<OrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(orderId)))
        return serviceHub.vaultService.queryBy<OrderState>(queryCriteria).states.single()
    }
    private fun outputState() : OrderState
    {
        return inputState().state.data.copy(status = Status.COMPLETED.name,
                transferredAt = Instant.now())
    }

    private fun transaction() : TransactionBuilder
    {
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(OrderContract.Commands.Send(), outputState().participants.map { it.owningKey })
        builder.addInputState(inputState())
        builder.addCommand(cmd)
        builder.addOutputState(outputState())

        return builder
    }
}

@InitiatedBy(SendTokenFlow::class)
class SendTokenFlowResponder(private val flowSession: FlowSession) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction {
        subFlow(object : SignTransactionFlow(flowSession)
        {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
            }
        })



        return subFlow(ReceiveFinalityFlow(flowSession)).also {
            when(flowSession.counterparty.name.organisation) {
                "IssuerPHP" -> subFlow(MergeFungibleTokenFlow("PHP"))
                "IssuerUSD" -> subFlow(MergeFungibleTokenFlow("USD"))
                else -> throw IllegalArgumentException("Merge unavailable!")
            }
        }
    }
}