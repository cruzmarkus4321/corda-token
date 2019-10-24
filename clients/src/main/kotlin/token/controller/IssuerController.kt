package token.controller

import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import token.dto.platform.SelfIssueTokenFlowDTO
import token.dto.platform.SendTokenFlowDTO
import token.dto.platform.VerifyOrderFlowDTO
import token.dto.token.TokenDTO
import token.service.IssuerService
import java.net.URI
import javax.validation.Valid

private const val CONTROLLER_NAME = "api/v1/issuer"
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(CONTROLLER_NAME)
class IssuerController(private val issuerService: IssuerService) : BaseController()
{
    @GetMapping(value = ["/order/all"], produces = ["application/json"])
    private fun getAllOrders() : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.getAllOrder()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @GetMapping(value = ["/order/{orderId}"], produces = ["application/json"])
    private fun getOrderById(@PathVariable orderId : String) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.getOrderById(orderId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @PatchMapping(value = ["/order/{verifyOrder}"], produces = ["application/json"])
    private fun verifyOrder(@PathVariable verifyOrder : VerifyOrderFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.verifyOrder(verifyOrder)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @PostMapping(value = ["/issue"], produces = ["application/json"])
    private fun selfIssueToken(@Valid @RequestBody selfIssueToken : SelfIssueTokenFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.selfIssueToken(selfIssueToken)
            ResponseEntity.created(URI("/$CONTROLLER_NAME/$response")).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Get all tokens
     */
    @GetMapping(value = ["/tokens"], produces = ["application/json"])
    private fun getAllTokens() : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.getAllTokens()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @PostMapping(value = ["/order/transfer"], produces = ["application/json"])
    private fun sendToken(@Valid @RequestBody sendToken : SendTokenFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.sendToken(sendToken)
            ResponseEntity.created(URI("/$CONTROLLER_NAME/${response.linearId}")).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }
}