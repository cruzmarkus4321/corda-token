package token.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import token.service.`interface`.IUserService

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
}