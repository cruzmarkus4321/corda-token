/*
package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.OrderContract
import com.template.contracts.UserContract
import com.template.functions.FlowFunctions
import com.template.states.OrderState
import com.template.states.UserState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@StartableByRPC
class TransferTokenFlow: FlowFunctions(){

    @Suspendable
    override fun call(): SignedTransaction {
        TODO()
    }

    private fun getAllOrderFromVault()
    {
        val order = getOrdersByLinearId()
    }

    private fun inOrderState(orderId: String): StateAndRef<OrderState>{
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(orderId)))
        return serviceHub.vaultService.queryBy<OrderState>(queryCriteria).states.single()
    }

    private fun inUserState(userId: String): StateAndRef<UserState>{
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(userId)))
        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
    }

    private fun outOrderState(state: StateAndRef<OrderState>): OrderState{
        return state.state.data.copy(transferredAt = Instant.now())
    }

    private fun outUserState(state: StateAndRef<UserState>, amount: String, currency: String): UserState{
        val wallet = state.state.data.wallet

        forEach

        return state.state.data.copy(
                wallet = )
        )
    }

    private fun transaction(orderId: String): TransactionBuilder {
        val builder = TransactionBuilder(getNotaries())
        val transferCmd = Command(OrderContract.Commands.Transfer(), ourIdentity.owningKey)
        val receiveCmd = Command(UserContract.Commands.Receive(), ourIdentity.owningKey)
        builder.addInputState(inOrderState(orderId))
                .addCommand(transferCmd)
                .addOutputState(outOrderState(inOrderState(orderId)))
        builder.addInputState(inUserState(inOrderState(orderId).state.data.userId))
                .addCommand(receiveCmd)
                .addOutputState(outUserState(inUserState(inOrderState(orderId).state.data.userId)))

        return builder
    }
}
*/
