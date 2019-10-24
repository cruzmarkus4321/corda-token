package com.template.functions

import com.template.states.OrderState
import com.template.states.ReserveOrderState
import com.template.states.UserState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import khttp.get

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

    fun getNotaries(): Party
    {
        return serviceHub.networkMapCache.notaryIdentities.first()
    }

    enum class Status
    {
        PENDING,
        VERIFIED,
        COMPLETED
    }

    fun getUserStateByLinearId(userId: String): StateAndRef<UserState>{
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(userId)))
        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
    }

    fun getOrderByLinearId(linearId: String) : StateAndRef<OrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(linearId)), status = Vault.StateStatus.ALL)
        return serviceHub.vaultService.queryBy<OrderState>(queryCriteria).states.single()
    }

    fun getReserveOrderStateByLinearId(reserveOrderId: String): StateAndRef<ReserveOrderState>
    {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(stringToUniqueIdentifier(reserveOrderId)))
        return serviceHub.vaultService.queryBy<ReserveOrderState>(queryCriteria).states.single()
    }

    fun getPHPRate(): Double{
        val response = get("https://api.exchangeratesapi.io/latest?base=USD&symbols=PHP")

        return response.jsonObject.getJSONObject("rates").getDouble("PHP")
    }

    fun getOtherCurrency(currency: String): String{
        return when (currency) {
            "PHP" -> "USD"
            "USD" -> "PHP"
            else -> throw IllegalArgumentException("Unsupported currency.")
        }
    }
}