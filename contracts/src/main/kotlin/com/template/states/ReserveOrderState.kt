package com.template.states

import com.template.contracts.ReserveOrderContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.time.Instant

@BelongsToContract(ReserveOrderContract::class)
data class ReserveOrderState(val userId: UniqueIdentifier,
                             val amount: Double,
                             val currency: String,
                             val orderedAt: Instant,
                             val transferredAt: Instant?,
                             override val linearId: UniqueIdentifier,
                             override val participants: List<Party>) : LinearState
