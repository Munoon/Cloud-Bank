package munoon.bank.service.transactional.user

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.user.UserTestData.assertMatch
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired

internal class UserServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userService: UserService

    @Test
    fun getProfileByToken() {
        val profile = userService.getProfileByToken("DEFAULT_USER")
        assertMatch(profile, UserTestData.DEFAULT_USER_TO)
    }
}