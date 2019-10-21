package token.service.`interface`

import token.dto.platform.ReserveOrderDTO
import token.dto.platform.ReserveOrderFlowDTO
import token.dto.user.RegisterUserFlowDTO
import token.dto.user.UserDTO

interface IUserService: IService{
    fun registerUser(request: RegisterUserFlowDTO): UserDTO
    fun addReserveOrder(request: ReserveOrderFlowDTO): ReserveOrderDTO
}