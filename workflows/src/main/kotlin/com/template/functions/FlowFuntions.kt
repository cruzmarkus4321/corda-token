package com.template.functions

import com.template.states.UserState
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

abstract class FlowFunctions : FlowLogic<SignedTransaction>()
{
    override val progressTracker = ProgressTracker(INITIALIZING, BUILDING, SIGNING, COLLECTING, FINALIZING)

    fun verifyAndSign(transaction: TransactionBuilder): SignedTransaction
    {
        progressTracker.currentStep = SIGNING
        transaction.verify(serviceHub)
        return serviceHub.signInitialTransaction(transaction)
    }

    fun stringToParty(name: String): Party
    {
        return serviceHub.identityService.partiesFromName(name, false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for $name")
    }

    fun stringToUniqueIdentifier(id: String): UniqueIdentifier
    {
        return UniqueIdentifier.fromString(id)
    }

    fun inputStateViaLinearId(linearId: String) : StateAndRef<UserState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(linearId)))
        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
    }

    /*fun registerStateCountBit(): Boolean {
        val count = serviceHub.vaultService.queryBy<UserState>().states.count()
        return count != 0
    }

    fun requestStateStatusBit(requestlinearId: String) : Striung
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(requestlinearId)))
        val requestState = serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()

        return requestState.state.data.
    }*/

    fun getNotaries(): Party
    {
        return serviceHub.networkMapCache.notaryIdentities.first()
    }
}