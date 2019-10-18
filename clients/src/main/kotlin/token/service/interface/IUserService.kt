package token.service.`interface`

import token.dto.user.RegisterUserFlowDTO
import token.dto.user.UserDTO

interface IUserService: IService{
    fun registerUser(registerUserDTO: RegisterUserFlowDTO): UserDTO
}