package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.move.MoveFungibleTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.move.MoveTokensFlowHandler
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokensHandler
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.heldTokenAmountCriteria
import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountWithIssuerCriteria
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
import com.template.states.ReserveOrderState
import com.template.types.TokenType
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class SendTokenFlow(private val reserveOrderId : String): FlowFunctions() {

    @Suspendable
    override fun call(): SignedTransaction {
        val partialTx = verifyAndSign(transaction())
        val otherPartySession = initiateFlow(stringToParty("Platform"))
        val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))

        subFlow(MoveIssuerTokenFlow(inputState().state.data.amount, ourIdentity, stringToParty("Platform")))

        return subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
    }

    private fun inputState() : StateAndRef<ReserveOrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(reserveOrderId)))
        return serviceHub.vaultService.queryBy<ReserveOrderState>(queryCriteria).states.single()
    }
    private fun outputState() : ReserveOrderState
    {
        return inputState().state.data.copy(status = Status.COMPLETED.name,
                transferredAt = Instant.now())
    }

    private fun transaction() : TransactionBuilder
    {
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(ReserveOrderContract.Commands.Send(), outputState().participants.map { it.owningKey })
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
        
        return subFlow(ReceiveFinalityFlow(flowSession))
    }
}