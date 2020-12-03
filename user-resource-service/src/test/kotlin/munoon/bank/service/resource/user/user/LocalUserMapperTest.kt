package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import munoon.bank.service.resource.user.user.UserTestData.DEFAULT_USER_ENTITY
import munoon.bank.service.resource.user.util.TestPasswordEncoder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

internal class LocalUserMapperTest {
    @Test
    fun asUser() {
        val user = LocalUserMapper.INSTANCE.asUser(DEFAULT_USER_ENTITY)
        val expected = User(100, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))
        assertThat(user).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    @Test
    fun registerUserEntity() {
        val userTo = AdminRegisterUserTo("Name", "Surname", "username", "password", setOf(UserRoles.ROLE_ADMIN))
        val actual = LocalUserMapper.INSTANCE.asUserEntity(userTo, TestPasswordEncoder())
        val expected = UserEntity(null, "Name", "Surname", "username", "{test}password", actual.registered, setOf(UserRoles.ROLE_ADMIN))
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    @Test
    fun updateUserEntity() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "newUsername", emptySet())
        val actual = LocalUserMapper.INSTANCE.asUserEntity(userTo, DEFAULT_USER_ENTITY)
        val expected = DEFAULT_USER_ENTITY.copy(name = "NewName", surname = "NewSurname", username = "newUsername", roles = emptySet())
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }
}