package com.template.contracts

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction


class ReserveOrderContract : Contract {

    companion object {
        const val id = "com.template.contracts.ReserveOrderContract"
    }
    override fun verify(tx: LedgerTransaction) {

        val command = tx.commands.requireSingleCommand<Commands>()

        when(command.value){
            is Commands.Reserve -> {
                requireThat {

                }
            }
        }

    }

    interface Commands : CommandData {
        class Reserve : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
    }
}