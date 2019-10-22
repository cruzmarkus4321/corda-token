package com.template.states

import com.template.contracts.ExchangeContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(ExchangeContract::class)
data class ExchangeState(val userId: UniqueIdentifier,
                         val amount: Double,
                         val currency: String,
                         override val linearId: UniqueIdentifier,
                         override val participants: List<Party>): LinearState