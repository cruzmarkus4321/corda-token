package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
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
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class VerifyOrderFlow(private val reserveOrderId : String) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction {
        val partialTx = verifyAndSign(transaction())
        val otherPartySession = initiateFlow(stringToParty("Platform"))
        val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))
        val fungibleToken = inputState().state.data.amount of TokenType("PHP") issuedBy ourIdentity heldBy ourIdentity

        return subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
                .also { subFlow(IssueTokensFlow(fungibleToken, listOf()))}
    }

    private fun inputState() : StateAndRef<ReserveOrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(reserveOrderId)))
        return serviceHub.vaultService.queryBy<ReserveOrderState>(queryCriteria).states.single()
    }

    private fun outputState() : ReserveOrderState
    {
        return inputState().state.data.copy(status = Status.VERIFIED.Value,
                verifiedAt = Instant.now())
    }

    private fun transaction() : TransactionBuilder
    {
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(ReserveOrderContract.Commands.Verify(), inputState().state.data.participants.map { it.owningKey })
        builder.addInputState(inputState())
        builder.addCommand(cmd)
        builder.addOutputState(outputState())

        return builder
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