package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.util.JsonUtils
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher
import java.time.LocalDateTime

object UserTestData {
    const val USER_ID = 100
    val DEFAULT_USER_ENTITY = UserEntity(USER_ID, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))
    val DEFAULT_USER = User(USER_ID, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))

    fun contentJson(expected: UserTo) = ResultMatcher {
        val actual = JsonUtils.readFromJson(it, UserTo::class.java)
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }
}