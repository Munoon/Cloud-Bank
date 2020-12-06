package munoon.bank.service.transactional.user

import org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE
import org.springframework.stereotype.Service

@Service
class UserService(private val userClient: UserClient) {
    fun getProfileByToken(token: String) = userClient.getProfile("$BEARER_TYPE $token")
}