package munoon.bank.service.resource.user.user

import munoon.bank.common.card.CardTo
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
    fun asToWithCards() {
        val card = CardTo("CARD", 100, "default", "123456789012", 10.0, true, true, LocalDateTime.now())
        val expected = UserToWithCards(100, "Nikita", "Ivchenko", "munoon", "10", LocalDateTime.now(), UserTestData.DEFAULT_USER.roles, listOf(card))
        val actual = LocalUserMapper.INSTANCE.asTo(UserTestData.DEFAULT_USER, listOf(card))
        assertMatch(actual, expected)
    }

    @Test
    fun asFullToWithCards() {
        val card = CardTo("CARD", 100, "default", "123456789012", 10.0, true, true, LocalDateTime.now())
        val expected = FullUserToWithCards(100, "Nikita", "Ivchenko", "munoon", "10", 100.0, LocalDateTime.now(), UserTestData.DEFAULT_USER.roles, listOf(card))
        val actual = LocalUserMapper.INSTANCE.asFullTo(UserTestData.DEFAULT_USER, listOf(card))
        assertMatch(actual, expected)
    }

    @Test
    fun registerUserEntity() {
        val userTo = AdminRegisterUserTo("Name", "Surname", "username", "password", "10", 100.0, setOf(UserRoles.ROLE_ADMIN))
        val actual = LocalUserMapper.INSTANCE.asUserEntity(userTo, TestPasswordEncoder())
        val expected = UserEntity(null, "Name", "Surname", "username", "{test}password", actual.registered, "10", 100.0, setOf(UserRoles.ROLE_ADMIN))
        assertMatch(actual, expected)
    }

    @Test
    fun updateUserEntity() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "newUsername", "10", 100.0, emptySet())
        val actual = LocalUserMapper.INSTANCE.asUserEntity(userTo, DEFAULT_USER_ENTITY)
        val expected = DEFAULT_USER_ENTITY.copy(name = "NewName", surname = "NewSurname", username = "newUsername", roles = emptySet())
        assertMatch(actual, expected)
    }
}