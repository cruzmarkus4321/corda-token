package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.amount
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlowHandler
import com.r3.corda.lib.tokens.workflows.flows.move.MoveFungibleTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountWithIssuerCriteria
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
import com.template.states.ReserveOrderState
import com.template.types.TokenType
import jdk.nashorn.internal.parser.Token
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import org.intellij.lang.annotations.Flow
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class ReserveOrderFlow( private val amount : String,
                        private val currency: String,
                        private val issuer: String) : FlowFunctions() {

    @Suspendable
    override fun call(): SignedTransaction {
        val partialTx = verifyAndSign(transaction())
        val otherPartySession = initiateFlow(stringToParty(issuer))
        val transactionSignedByParties = subFlow(CollectSignaturesFlow(partialTx, listOf(otherPartySession)))

        return subFlow(FinalityFlow(transactionSignedByParties, listOf(otherPartySession)))
    }

    private fun outputState() : ReserveOrderState
    {
        return ReserveOrderState(
                amount = amount.toDouble(),
                currency = currency,
                status = Status.PENDING.Value,
                orderedAt = Instant.now(),
                verifiedAt = null,
                transferredAt = null,
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity, stringToParty(issuer))
        )
    }

    private fun transaction() : TransactionBuilder
    {
        val builder = TransactionBuilder(getNotaries())
        val cmd = Command(ReserveOrderContract.Commands.Reserve(), outputState().participants.map { it.owningKey } )
        builder.addOutputState(outputState())
        builder.addCommand(cmd)

        return builder
    }
}

@InitiatedBy(ReserveOrderFlow::class)
class ReserveOrderFlowResponder(private val flowSession : FlowSession) : FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction {
        subFlow(object : SignTransactionFlow(flowSession)
        {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
            }
        })
//        val fungibleToken = 100 of TokenType("PHP") issuedBy ourIdentity heldBy ourIdentity

        /*val holderSession = initiateFlow(ourIdentity)
        val otherHolderSession = initiateFlow(stringToParty("Platform"))*/

        return subFlow(ReceiveFinalityFlow(flowSession))
//                .also { subFlow(IssueTokensFlow(fungibleToken, listOf())) }
    }
}
