package munoon.bank.service.transactional.user

import munoon.bank.common.user.UserTo
import munoon.bank.common.util.exception.NotFoundException
import org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE
import org.springframework.stereotype.Service

@Service
class UserService(private val userClient: UserClient) {
    fun getProfileByToken(token: String) = userClient.getProfile("$BEARER_TYPE $token")

    fun getUsersById(usersId: Set<Int>): Map<Int, UserTo?> = userClient.getUsersById(usersId.toList())
            .map { it.id to it }
            .toMap()

    fun getUserById(userId: Int): UserTo = getUsersById(setOf(userId))[userId]
            ?: throw NotFoundException("User with id $userId not found!")
}