package token.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import token.dto.reserveorder.ReserveOrderDTO
import token.dto.reserveorder.ReserveOrderFlowDTO
import token.service.`interface`.IReserveOrderService
import java.net.URI
import javax.validation.Valid

private const val CONTROLLER_NAME = "api/v1/reserveorders"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(CONTROLLER_NAME)
class ReserveOrderController (private val reserveOrderService: IReserveOrderService): BaseController()
{
    /**
     * Get all reserve orders
     */
    @GetMapping(value = ["/all"], produces = ["application/json"])
    private fun getAllReserveOrders(): ResponseEntity<Any>
    {
        return try {
            val response = reserveOrderService.getAll()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Get a reserve order using linearId
     */
    @GetMapping(value = ["/{reserveOrderId}"], produces = ["application/json"])
    private fun getReserveOrder(@PathVariable reserveOrderId: String): ResponseEntity<Any>
    {
        return try {
            val response = reserveOrderService.get(reserveOrderId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Add a reserve order
     */
    @PostMapping(value = [], produces = ["application/json"])
    private fun registerUser(@Valid @RequestBody request: ReserveOrderFlowDTO): ResponseEntity<Any>
    {
        return try {
            val response = reserveOrderService.addReserveOrder(request)
            ResponseEntity.created(URI("/" + CONTROLLER_NAME + "/" + response.linearId)).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }
}