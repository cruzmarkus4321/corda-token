package token.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import token.dto.order.OrderFlowDTO
import token.service.`interface`.IOrderService
import java.net.URI

private const val CONTROLLER_NAME = "api/v1/orders"
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(CONTROLLER_NAME)
class OrderController(private val orderService: IOrderService) : BaseController()
{
    /**
     * Get all users
     */
    @GetMapping(value = ["/all"], produces = ["application/json"])
    private fun getAllOrders() : ResponseEntity<Any>
    {
        return try {
            val response = orderService.getAll()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Get an order using linearId
     */
    @GetMapping(value = ["/{orderId}"], produces = ["application/json"])
    private fun getOrderById(@PathVariable orderId : String) : ResponseEntity<Any>
    {
        return try {
            val response = orderService.get(orderId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Add an order
     */
    @PostMapping(value = [], produces = ["application/json"])
    private fun addOrder(@RequestBody request : OrderFlowDTO) : ResponseEntity<Any>
    {
        return try {
            val response = orderService.addOrder(request)
            ResponseEntity.created(URI("/" + CONTROLLER_NAME + "/" + response.linearId)).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }
}