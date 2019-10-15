package com.template.contracts

import net.corda.core.contracts.CommandAndState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction


class OrderContract : Contract {

    companion object {
        const val id = "com.template.contracts.OrderContract"
    }
    override fun verify(tx: LedgerTransaction) {

    }

    interface Commands : CommandData {
        class Order : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
    }
}