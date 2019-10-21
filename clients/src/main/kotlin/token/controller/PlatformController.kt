package token.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import token.dto.platform.OrderFlowDTO
import token.dto.platform.TransferReserveOrderFlowDTO

import token.service.`interface`.IPlatformService
import java.net.URI
import javax.validation.Valid

private const val CONTROLLER_NAME = "api/v1/platform"
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(CONTROLLER_NAME)
class OrderController(private val platformService: IPlatformService) : BaseController()
{
    /**
     * Get all orders
     */
    @GetMapping(value = ["/orders/all"], produces = ["application/json"])
    private fun getAllOrders() : ResponseEntity<Any>
    {
        return try {
            val response = platformService.getAllOrders()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Get an order using linearId
     */
    @GetMapping(value = ["/orders/{orderId}"], produces = ["application/json"])
    private fun getOrderById(@PathVariable orderId : String) : ResponseEntity<Any>
    {
        return try {
            val response = platformService.getOrder(orderId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Add an order
     */
    @PostMapping(value = ["/orders"], produces = ["application/json"])
    private fun addOrder(@RequestBody request : OrderFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = platformService.addOrder(request)
            ResponseEntity.created(URI("/$CONTROLLER_NAME/${response.linearId}")).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Get all reserve orders
     */
    @GetMapping(value = ["/reserveorders/all"], produces = ["application/json"])
    private fun getAllReserveOrders(): ResponseEntity<Any>
    {
        return try {
            val response = platformService.getAllReserveOrders()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Get a reserve order using linearId
     */
    @GetMapping(value = ["/reserveorders/{reserveOrderId}"], produces = ["application/json"])
    private fun getReserveOrder(@PathVariable reserveOrderId: String): ResponseEntity<Any>
    {
        return try {
            val response = platformService.getReserveOrder(reserveOrderId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Transfer token to User
     */
    @PostMapping(value = ["/transfer"], produces = ["application/json"])
    private fun transferTokens(@RequestBody request: TransferReserveOrderFlowDTO): ResponseEntity<Any>
    {
        return try {
            val response = platformService.transferToken(request)
            ResponseEntity.created(URI("/$CONTROLLER_NAME/${response.linearId}")).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }
}