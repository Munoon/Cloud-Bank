package munook.bank.service.market.util

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import org.springframework.security.oauth2.provider.OAuth2Request
import java.time.LocalDateTime

object TestUtils {
    val DEFAULT_USER = User(
        100, "Nikita", "Ivchenko", "munoon", "{noop}password", "10", 100.0, LocalDateTime.now(),
        hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))

    val DEFAULT_OAUTH_REQUEST = OAuth2Request(
        mapOf("client_id" to "web"),
        "web", emptySet(),
        true, setOf("user_info"),
        emptySet(), "http://localhost:8080",
        emptySet(), emptyMap()
    )
}