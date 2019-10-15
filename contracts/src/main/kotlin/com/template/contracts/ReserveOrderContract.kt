package com.template.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class ReserveOrderContract : Contract {
    companion object {
        const val id = "com.template.contracts.ReserveOrderContract"
    }

    override fun verify(tx: LedgerTransaction) {

    }

    interface Commands : CommandData
    {
        class Reserve : TypeOnlyCommandData(), Commands
        class Verify : TypeOnlyCommandData(), Commands
        class Send : TypeOnlyCommandData(), Commands
    }
}