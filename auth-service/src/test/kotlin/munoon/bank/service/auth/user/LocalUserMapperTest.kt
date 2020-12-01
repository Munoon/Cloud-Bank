package munoon.bank.service.auth.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class LocalUserMapperTest {
    @Test
    fun asUser() {
        val entity = UserEntity(100, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), sortedSetOf(UserRoles.ROLE_COURIER, UserRoles.ROLE_BARMEN, UserRoles.ROLE_TEACHER, UserRoles.ROLE_ADMIN))
        val user = entity.asUser()
        val expected = User(100, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), sortedSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))
        assertThat(user).usingRecursiveComparison().ignoringFields("registered", "roles").isEqualTo(expected)
        assertThat(user.roles).usingDefaultElementComparator().isEqualTo(expected.roles)
    }
}