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
        return if(getReserveOrderStateById(reserveOrderId).state.data.transferredAt == null){
            subFlow(UpdatePlatformTokenFlow(reserveOrderId))

            subFlow(TransferTokenToWalletFlow(reserveOrderId))

            subFlow(FinalityFlow(verifyAndSign(transaction(reserveOrderId)), listOf()))

            subFlow(RecordHistoryFlow(reserveOrderId))
        } else {
            throw IllegalArgumentException("This reserve order is already transferred at ${getReserveOrderStateById(reserveOrderId).state.data.transferredAt}")
        }
    }

    private fun outputState(reserveOrderId: String): ReserveOrderState
    {
        val reserveStateData = getReserveOrderStateById(reserveOrderId).state.data

        return reserveStateData.copy(transferredAt = Instant.now())
    }

    private fun transaction(reserveOrderId: String): TransactionBuilder
    {
        val cmd = Command(ReserveOrderContract.Commands.Transfer(), ourIdentity.owningKey)
        val builder = TransactionBuilder(getNotaries())
                .addInputState(getReserveOrderStateById(reserveOrderId))
                .addCommand(cmd)
                .addOutputState(outputState(reserveOrderId))

        return builder
    }
}
