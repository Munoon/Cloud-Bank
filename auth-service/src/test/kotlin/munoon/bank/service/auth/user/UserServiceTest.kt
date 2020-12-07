package munoon.bank.service.auth.user

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.user.UserMapper
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.auth.AbstractTest
import munoon.bank.service.auth.user.UserTestData.USER_ID
import munoon.bank.service.auth.user.UserTestData.USER_USERNAME
import munoon.bank.service.auth.user.UserTestData.assertMatch
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import munoon.bank.service.auth.user.UserTestData.USER as DEFAULT_USER

internal class UserServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userService: UserService

    @Test
    fun loadUserByUsername() {
        val details = userService.loadUserByUsername(USER_USERNAME)
        val user = (details as AuthorizedUser).user
        val expected = UserMapper.INSTANCE.asTo(DEFAULT_USER)
        assertMatch(user, expected)
    }

    @Test
    fun loadUserByUsernameNotFound() {
        assertThrows(UsernameNotFoundException::class.java) { userService.loadUserByUsername("UNKNOWN") }
    }

    @Test
    fun getById() {
        val user = userService.getById(USER_ID)
        assertMatch(user, DEFAULT_USER)
    }

    @Test
    fun getByIdNotFound() {
        assertThrows(NotFoundException::class.java) { userService.getById(999) }
    }
}