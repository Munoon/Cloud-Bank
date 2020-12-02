package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class LocalUserMapperTest {
    @Test
    fun asUser() {
        val user = LocalUserMapper.INSTANCE.asUser(UserTestData.DEFAULT_USER_ENTITY)
        val expected = User(100, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))
        assertThat(user).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }
}