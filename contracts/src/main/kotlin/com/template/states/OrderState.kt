package com.template.states

import com.template.contracts.OrderContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.time.Instant

@BelongsToContract(OrderContract::class)
data class OrderState(val amount: Double,
                      val currency: String,
                      val status: String, //PENDING, VERIFIED, COMPLETED
                      val orderedAt: Instant,
                      val verifiedAt: Instant?,
                      val transferredAt: Instant?,
                      override val linearId: UniqueIdentifier,
                      override val participants: List<Party>) : LinearState