package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import munoon.bank.service.resource.user.user.UserTestData.DEFAULT_USER_ENTITY
import munoon.bank.service.resource.user.user.UserTestData.assertMatch
import munoon.bank.service.resource.user.util.TestPasswordEncoder
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class LocalUserMapperTest {
    @Test
    fun asUser() {
        val user = LocalUserMapper.INSTANCE.asUser(DEFAULT_USER_ENTITY)
        val expected = User(UserTestData.DEFAULT_USER)
        assertMatch(user, expected)
    }

    @Test
    fun registerUserEntity() {
        val userTo = AdminRegisterUserTo("Name", "Surname", "username", "password", "10", setOf(UserRoles.ROLE_ADMIN))
        val actual = LocalUserMapper.INSTANCE.asUserEntity(userTo, TestPasswordEncoder())
        val expected = UserEntity(null, "Name", "Surname", "username", "{test}password", actual.registered, "10", setOf(UserRoles.ROLE_ADMIN))
        assertMatch(actual, expected)
    }

    @Test
    fun updateUserEntity() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "newUsername", "10", emptySet())
        val actual = LocalUserMapper.INSTANCE.asUserEntity(userTo, DEFAULT_USER_ENTITY)
        val expected = DEFAULT_USER_ENTITY.copy(name = "NewName", surname = "NewSurname", username = "newUsername", roles = emptySet())
        assertMatch(actual, expected)
    }
}