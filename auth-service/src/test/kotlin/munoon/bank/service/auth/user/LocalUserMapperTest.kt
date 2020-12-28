package munoon.bank.service.auth.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import munoon.bank.service.auth.user.UserTestData.assertMatch
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class LocalUserMapperTest {
    @Test
    fun asUser() {
        val entity = UserEntity(100, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), "10", 100.0, hashSetOf(UserRoles.ROLE_COURIER, UserRoles.ROLE_BARMEN, UserRoles.ROLE_TEACHER, UserRoles.ROLE_ADMIN))
        val user = entity.asUser()
        val expected = User(UserTestData.USER)
        assertMatch(user, expected)
    }
}