package munoon.bank.service.auth.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import java.time.LocalDateTime

object UserTestData {
    const val USER_ID = 100
    const val USER_USERNAME = "munoon"
    const val USER_PASSWORD = "password"
    val USER = User(USER_ID, "Nikita", "Ivchenko", USER_USERNAME, "{noop}" + USER_PASSWORD, LocalDateTime.now(), setOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))
}