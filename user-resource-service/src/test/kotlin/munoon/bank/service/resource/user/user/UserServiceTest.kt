package munoon.bank.service.resource.user.user

import munoon.bank.service.resource.user.AbstractTest
import munoon.bank.service.resource.user.util.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UserServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userService: UserService

    @Test
    fun getById() {
        val user = userService.getById(UserTestData.USER_ID)
        assertThat(user).usingRecursiveComparison().ignoringFields("registered").isEqualTo(UserTestData.DEFAULT_USER)
    }

    @Test
    fun getByIdNotFound() {
        assertThrows(NotFoundException::class.java) { userService.getById(999) }
    }
}