package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.service.resource.user.util.NotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun getById(id: Int): User = userRepository.findById(id)
            .map { it.asUser() }
            .orElseThrow { NotFoundException("User with id $id is not found!") }
}