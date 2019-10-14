package com.template.states

import com.template.contracts.OrderContract
import com.template.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import java.time.Instant

@BelongsToContract(OrderContract::class)
data class OrderState(val userId: String,
                 val amount: Double,
                 val currency: String,
                 val orderedAt: Instant,
                 val transferredAt: Instant?,
                 override val linearId: UniqueIdentifier,
                 override val participants: List<AbstractParty>) : LinearState