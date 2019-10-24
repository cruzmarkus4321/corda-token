package com.template.contracts

import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class HistoryContract: Contract {
    companion object
    {
        const val id = "com.template.contracts.HistoryContract"
    }

    override fun verify(tx: LedgerTransaction)
    {

    }

    interface Commands
    {
        class Save: TypeOnlyCommandData(), Commands
    }
}