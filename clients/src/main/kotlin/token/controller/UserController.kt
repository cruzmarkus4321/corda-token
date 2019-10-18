package token.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import token.dto.user.RegisterUserFlowDTO
import token.dto.user.UserDTO
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
}