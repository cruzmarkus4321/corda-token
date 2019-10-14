package com.template.states

import com.template.contracts.ReserveOrderContract
import com.template.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.time.Instant
import java.util.*

@BelongsToContract(ReserveOrderContract::class)
data class ReserveOrderState(val amount: Double,
                        val currency: String,
                        val status: String, // PENDING, VERIFIED, COMPLETED
                        val orderedAt: Instant,
                        val verifiedAt: Instant?,
                        val transferredAt: Instant?,
                        override val linearId: UniqueIdentifier,
                        override val participants: List<Party>) : LinearState