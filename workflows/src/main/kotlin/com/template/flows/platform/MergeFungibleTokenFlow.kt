package com.template.flows.platform


import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountCriteria
import com.template.functions.FlowFunctions
import com.template.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction

@StartableByRPC
class MergeFungibleTokenFlow(private val tokenIdentifier: String) : FlowFunctions()
{

    @Suspendable
    override fun call(): SignedTransaction {

        return subFlow(MoveFungibleTokens(
                partyAndAmount = PartyAndAmount(ourIdentity, tokenAmount() of TokenType(tokenIdentifier)),
                queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
        ))
    }

    private fun getAllExistingToken() : List<StateAndRef<FungibleToken>>
    {
        val queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
        val fungibleTokenRef = serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states
        return fungibleTokenRef.toList()
    }

    private fun tokenAmount() : Double
    {
        var amount = 0.toDouble()

        getAllExistingToken().forEach {
            amount += it.state.data.amount.toDecimal().toDouble()
        }
        return if(amount > 0)amount else throw IllegalArgumentException("")
    }

}
//
//@InitiatedBy(MergeFungibleTokenFlow::class)
//class MergeFungibleTokenFlowResponder(private val flowSession: FlowSession): FlowLogic<Unit>() {
//
//    @Suspendable
//    override fun call(): Unit {
//        return subFlow(MoveTokensFlowHandler(flowSession))
//    }
//}




//package com.template.flows.platform
//
//import co.paralleluniverse.fibers.Suspendable
//import com.r3.corda.lib.tokens.contracts.states.FungibleToken
//import com.r3.corda.lib.tokens.contracts.utilities.heldBy
//import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
//import com.r3.corda.lib.tokens.contracts.utilities.of
//import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
//import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens
//import com.r3.corda.lib.tokens.workflows.utilities.tokenAmountCriteria
//import com.template.functions.FlowFunctions
//import com.template.types.TokenType
//import net.corda.core.contracts.StateAndRef
//import net.corda.core.flows.StartableByRPC
//import net.corda.core.node.services.queryBy
//import net.corda.core.transactions.SignedTransaction
//import javax.swing.JOptionPane
//
//@StartableByRPC
//class MergeFungibleTokenFlow(private val tokenIdentifier : String) : FlowFunctions() {
//
//    private var tokenIssuer : String = ""
//
//    @Suspendable
//    override fun call(): SignedTransaction {
//
//        tokenIssuer = when(tokenIdentifier) {
//            "PHP" -> "IssuerPHP"
//            "USD" -> "IssuerUSD"
//            else -> throw IllegalArgumentException("Cannot recognized Token Identifier.")
//        }
//
//        JOptionPane.showMessageDialog(null, "${tokenIdentifier} ${tokenIssuer}")
//        val newToken = newToken()
//        JOptionPane.showMessageDialog(null, newToken)
//
//        var existingToken = getExistingToken()
//
//        while(existingToken != null){
//            JOptionPane.showMessageDialog(null, existingToken != null)
//            JOptionPane.showMessageDialog(null, existingToken)
//            subFlow(RedeemFungibleTokens(
//                amount = existingToken.amount.toDecimal() of existingToken.tokenType,
//                issuer = existingToken.issuer,
//                queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
//            ))
//            existingToken = getExistingToken()
//        }
//
//        JOptionPane.showMessageDialog(null, newToken)
//        return subFlow(IssueTokensFlow(newToken, listOf()))
//    }
//
//    private fun getAllExistingToken() : List<StateAndRef<FungibleToken>>
//    {
//        val queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
//        val fungibleTokenRef = serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states
//        return fungibleTokenRef.toList()
//    }
//
//    private fun getExistingToken() : FungibleToken?
//    {
//        val queryCriteria = tokenAmountCriteria(TokenType(tokenIdentifier))
//        val fungibleTokenRef = serviceHub.vaultService.queryBy<FungibleToken>(queryCriteria).states.firstOrNull()
//        return fungibleTokenRef?.state?.data
//    }
//
//    private fun newToken() : FungibleToken
//    {
//        var amount = 0.0
//        getAllExistingToken().forEach {
//            amount += it.state.data.amount.toDecimal().toDouble()
//        }
//        return amount of TokenType(tokenIdentifier) issuedBy ourIdentity heldBy ourIdentity
//    }
//}