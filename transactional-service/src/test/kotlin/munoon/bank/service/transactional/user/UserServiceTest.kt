package munoon.bank.service.transactional.user

import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.user.UserTestData.assertMatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class UserServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userService: UserService

    @Test
    fun getUsersById() {
        val users = userService.getUsersById(setOf(100))
        assertThat(users).containsOnlyKeys(100)
        assertMatch(users[100]!!, UserTestData.DEFAULT_USER_TO)
    }

    @Test
    fun getUserById() {
        assertMatch(userService.getUserById(100), UserTestData.DEFAULT_USER_TO)
    }

    @Test
    fun getUserByIdNotFound() {
        assertThrows<NotFoundException> {
            userService.getUserById(999)
        }
    }

    @Test
    fun getUserByIdOrNull() {
        assertMatch(userService.getUserOrNull(100)!!, UserTestData.DEFAULT_USER_TO)
    }

    @Test
    fun getUserByIdNull() {
        assertThat(userService.getUserOrNull(999)).isNull()
    }
}