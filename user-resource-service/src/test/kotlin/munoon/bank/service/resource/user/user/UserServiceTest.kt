package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.service.resource.user.AbstractTest
import munoon.bank.service.resource.user.user.UserTestData.DEFAULT_USER
import munoon.bank.service.resource.user.user.UserTestData.USER_ID
import munoon.bank.service.resource.user.util.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

internal class UserServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun getById() {
        val user = userService.getById(UserTestData.USER_ID)
        assertThat(user).usingRecursiveComparison().ignoringFields("registered").isEqualTo(DEFAULT_USER)
    }

    @Test
    fun getByIdNotFound() {
        assertThrows(NotFoundException::class.java) { userService.getById(999) }
    }

    @Test
    fun getAll() {
        with(userService.getAll(PageRequest.of(0, 1))) {
            assertThat(totalElements).isEqualTo(1)
            assertThat(content).usingElementComparatorIgnoringFields("registered").isEqualTo(listOf(DEFAULT_USER))
        }

        val emptyRequest = userService.getAll(PageRequest.of(1, 1))
        assertThat(emptyRequest.totalElements).isEqualTo(1)
        assertThat(emptyRequest.content.size).isEqualTo(0)
    }

    @Test
    fun createUser() {
        val userTo = AdminRegisterUserTo("New", "User", "username", "password", emptySet())
        val createUser = userService.createUser(userTo)

        val expected = User(createUser.id, "New", "User", "username", createUser.password, createUser.registered, emptySet())
        assertThat(userService.getAll(PageRequest.of(0, 10)))
                .usingElementComparatorIgnoringFields("password", "registered")
                .isEqualTo(listOf(DEFAULT_USER, expected))

        assertThat(passwordEncoder.matches("password", createUser.password)).isTrue()
    }

    @Test
    fun updateUser() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", emptySet())
        userService.updateUser(USER_ID, userTo)

        val expected = User(100, "NewName", "NewSurname", "test", "password", LocalDateTime.now(), emptySet())
        val actual = userService.getById(USER_ID)
        assertThat(actual).usingRecursiveComparison().ignoringFields("password", "registered").isEqualTo(expected)
    }

    @Test
    fun updateUserNotFound() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", emptySet())
        assertThrows(NotFoundException::class.java) { userService.updateUser(999, userTo) }
    }

    @Test
    fun updateUserPassword() {
        userService.updateUser(USER_ID, AdminUpdateUserPasswordTo("newPassword"))

        val userPassword = userService.getById(USER_ID).password
        assertThat(passwordEncoder.matches("newPassword", userPassword)).isTrue()
    }

    @Test
    fun updateUserPasswordNotFound() {
        assertThrows(NotFoundException::class.java) {
            userService.updateUser(999, AdminUpdateUserPasswordTo("newPassword"))
        }
    }

    @Test
    fun removeUser() {
        with (userService.getAll(PageRequest.of(0, 1))) {
            assertThat(totalElements).isEqualTo(1)
            assertThat(content.size).isEqualTo(1)
        }

        userService.removeUser(USER_ID)

        with (userService.getAll(PageRequest.of(0, 1))) {
            assertThat(totalElements).isEqualTo(0)
            assertThat(content.size).isEqualTo(0)
        }
    }

    @Test
    fun removeUserNotFound() {
        assertThrows(NotFoundException::class.java) { userService.removeUser(999) }
    }
}