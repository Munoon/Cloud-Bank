package munoon.bank.service.transactional.user

import munoon.bank.common.user.UserTo
import munoon.bank.common.util.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class UserService(private val restTemplate: RestTemplate) {
    fun getUsersById(usersId: Set<Int>): Map<Int, UserTo?> {
        val usersIdsParam = usersId.joinToString(",")
        return restTemplate.getForObject<Array<UserTo>>("http://user-resource-service/microservices/users?ids=$usersIdsParam")
                .map { it.id to it }
                .toMap()
    }

    fun getUserById(userId: Int): UserTo = getUsersById(setOf(userId))[userId]
            ?: throw NotFoundException("User with id $userId not found!")
}