package com.template.contracts

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class UserContract: Contract{
    companion object{
        const val id = "com.template.contracts.UserContract"
    }

    override fun verify(tx: LedgerTransaction) {

    }

    interface Commands: CommandData
    {
        class Add: TypeOnlyCommandData(), Commands
        class Receive: TypeOnlyCommandData(), Commands
        class Withdraw: TypeOnlyCommandData(), Commands
    }

}