package com.template.states

import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.template.contracts.UserContract
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.time.Instant

@BelongsToContract(UserContract::class)
data class UserState(val name: String,
                     val wallet: MutableList<Amount<TokenType>>,
                     val registeredDate: Instant,
                     override val linearId: UniqueIdentifier,
                     override val participants: List<Party>): LinearState