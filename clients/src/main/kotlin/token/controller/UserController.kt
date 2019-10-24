package token.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import token.dto.platform.ReserveOrderFlowDTO
import token.dto.user.ExchangeTokenFlowDTO
import token.dto.user.RegisterUserFlowDTO
import token.service.`interface`.IUserService
import java.net.URI
import javax.validation.Valid

private const val CONTROLLER_NAME = "api/v1/users"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(CONTROLLER_NAME)
class UserController (private val userService: IUserService): BaseController()
{
     /**
     * Get all users
     */
    @GetMapping(value = ["/all"], produces = ["application/json"])
    private fun getAllUsers(): ResponseEntity<Any>
    {
        return try {
            val response = userService.getAll()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
    * Get a user using linearId
    */
    @GetMapping(value = ["/{userId}"], produces = ["application/json"])
    private fun getUser(@PathVariable userId: String): ResponseEntity<Any>
    {
        return try {
            val response = userService.get(userId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Add a user
     */
    @PostMapping(value = [], produces = ["application/json"])
    private fun registerUser(@RequestBody request: RegisterUserFlowDTO): ResponseEntity<Any>
    {
        return try {
            val response = userService.registerUser(request)
            ResponseEntity.created(URI("/" + CONTROLLER_NAME + "/" + response.linearId)).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Add a reserve order
     */
    @PostMapping(value = ["/reserveorder"], produces = ["application/json"])
    private fun addReserveOrder(@Valid @RequestBody request: ReserveOrderFlowDTO): ResponseEntity<Any>
    {
        return try {
            val response = userService.addReserveOrder(request)
            ResponseEntity.created(URI("/$CONTROLLER_NAME/${response.linearId}")).body(response)
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    /**
     * Exchange token
     */
    @PostMapping(value = ["/exchange"], produces = ["application/json"])
    private fun exchangeToken(@RequestBody request: ExchangeTokenFlowDTO): ResponseEntity<Any>
    {
        return try {
            val response = userService.exchangeToken(request)
            ResponseEntity.created(URI("/$CONTROLLER_NAME/${response.linearId}")).body(response)
        }catch (e: Exception) {
            this.handleException(e)
        }
    }
}