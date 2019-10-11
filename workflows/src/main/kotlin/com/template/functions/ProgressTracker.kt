package com.template.functions

import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.utilities.ProgressTracker

object INITIALIZING : ProgressTracker.Step("Performing initial steps.")
object BUILDING : ProgressTracker.Step("Building and verifying transaction.")
object SIGNING : ProgressTracker.Step("Signing transaction.")
object COLLECTING : ProgressTracker.Step("Collecting counterparty signature.") {
    override fun childProgressTracker() = CollectSignaturesFlow.tracker()
}
object FINALIZING : ProgressTracker.Step("Finalizing transaction.") {
    override fun childProgressTracker() = FinalityFlow.tracker()
}