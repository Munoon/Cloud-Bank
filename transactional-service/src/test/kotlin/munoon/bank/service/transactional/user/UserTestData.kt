package munoon.bank.service.transactional.user

import munoon.bank.common.user.UserRoles
import munoon.bank.common.user.UserTo
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

object UserTestData {
    const val USER_ID = 100
    val DEFAULT_USER = UserTo(USER_ID, "Nikita", "Ivchenko", "munoon", "10", LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))

    fun assertMatch(actual: UserTo, expected: UserTo) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    fun assertMatch(actual: List<UserTo>, expected: List<UserTo>) {
        assertThat(actual).usingElementComparatorIgnoringFields("registered").isEqualTo(expected.toList())
    }

    fun assertMatch(actual: List<UserTo>, vararg expected: UserTo) {
        assertMatch(actual, expected.toList())
    }
}