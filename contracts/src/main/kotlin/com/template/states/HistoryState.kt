package com.template.states

import com.template.contracts.HistoryContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.time.Instant
import java.util.*

@BelongsToContract(HistoryContract::class)
data class HistoryState(val amount: Double,
                        val currency: String,
                        val userId: UniqueIdentifier,
                        val transferredAt: Instant,
                        override val linearId: UniqueIdentifier,
                        override val participants: List<Party>): LinearState