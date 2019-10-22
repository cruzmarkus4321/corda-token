package com.template.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class ExchangeContract: Contract {
    companion object
    {
        const val id = "com.template.contracts.ExchangeContract"
    }

    override fun verify(tx: LedgerTransaction)
    {

    }

    interface Commands: CommandData
    {
        class Exchange : TypeOnlyCommandData(), Commands
    }

}