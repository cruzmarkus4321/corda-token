package com.template.flows.platform

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.ReserveOrderContract
import com.template.functions.FlowFunctions
import com.template.states.ReserveOrderState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.lang.IllegalArgumentException
import java.time.Instant

@StartableByRPC
class TransferTokenFlow(private val reserveOrderId: String): FlowFunctions(){

    @Suspendable
    override fun call(): SignedTransaction {
        return if(inputState(reserveOrderId).state.data.transferredAt == null){

            subFlow(UpdatePlatFormTokenFlow(reserveOrderId))

            subFlow(TransferTokenToWalletFlow(reserveOrderId))

            subFlow(FinalityFlow(verifyAndSign(transaction(reserveOrderId)), listOf()))
        } else {
            throw IllegalArgumentException("This reserve order is already transferred at ${inputState(reserveOrderId).state.data.transferredAt}")
        }
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
