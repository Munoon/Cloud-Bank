package munoon.bank.service.auth.user

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.user.UserMapper
import munoon.bank.service.auth.AbstractTest
import munoon.bank.service.auth.user.UserTestData.USER_USERNAME
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
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
        assertThat(user).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    @Test
    fun loadUserByUsernameNotFound() {
        assertThrows(UsernameNotFoundException::class.java) { userService.loadUserByUsername("UNKNOWN") }
    }
}