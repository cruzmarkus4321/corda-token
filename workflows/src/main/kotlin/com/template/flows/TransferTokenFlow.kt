package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.heldTokenAmountCriteria
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
import com.template.states.OrderState
import com.template.states.ReserveOrderState
import com.template.types.TokenType
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import org.hibernate.Transaction
import java.time.Instant

@StartableByRPC
class TransferTokenFlow(private val reserveOrderId: String): FlowFunctions(){

    @Suspendable
    override fun call(): SignedTransaction {
        subFlow(UpdatePlatFormTokenFlow(reserveOrderId))

        subFlow(TransferTokenToWalletFlow(reserveOrderId))

        return subFlow(FinalityFlow(verifyAndSign(transaction(reserveOrderId)), listOf()))
    }

    private fun inputState(reserveOrderId: String): StateAndRef<ReserveOrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(reserveOrderId)))
        return serviceHub.vaultService.queryBy<ReserveOrderState>(queryCriteria).states.single()
    }

    private fun outputState(reserveOrderId: String): ReserveOrderState
    {
        val reserveStateData = inputState(reserveOrderId).state.data

        return reserveStateData.copy(transferredAt = Instant.now())
    }

    private fun transaction(reserveOrderId: String): TransactionBuilder
    {
        val cmd = Command(ReserveOrderContract.Commands.Transfer(), ourIdentity.owningKey)
        val builder = TransactionBuilder(getNotaries())
                .addInputState(inputState(reserveOrderId))
                .addCommand(cmd)
                .addOutputState(outputState(reserveOrderId))

        return builder
    }
}
