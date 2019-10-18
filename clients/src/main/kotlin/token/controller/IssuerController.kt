package token.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import token.dto.order.SelfIssueTokenFlowDTO
import token.dto.order.SendTokenFlowDTO
import token.dto.order.VerifyOrderFlowDTO
import token.service.IssuerService
import javax.validation.Valid

private const val CONTROLLER_NAME = "api/v1/issuer"
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(CONTROLLER_NAME)
class IssuerController(private val issuerService: IssuerService) : BaseController()
{
    @GetMapping(value = "/order/all", produces = ["application/json"])
    private fun getAllOrders() : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.getAllOrder()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @GetMapping(value = "/order/{orderId}")
    private fun getOrderById(@PathVariable orderId : String) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.getOrderById(orderId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @PatchMapping(value = "/order/verify")
    private fun verifyOrder(@Valid @RequestBody verifyOrder : VerifyOrderFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.verifyOrder(verifyOrder)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @PostMapping(value = "/issue")
    private fun selfIssueToken(@Valid @RequestBody selfIssueToken : SelfIssueTokenFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.selfIssueToken(selfIssueToken)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    @PatchMapping(value = "/order/send")
    private fun sendToken(@Valid @RequestBody sendToken : SendTokenFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = issuerService.sendToken(sendToken)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }
}