package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.service.resource.user.AbstractTest
import munoon.bank.service.resource.user.user.UserTestData.DEFAULT_USER
import munoon.bank.service.resource.user.user.UserTestData.USER_ID
import munoon.bank.service.resource.user.user.UserTestData.USER_PASSWORD
import munoon.bank.service.resource.user.user.UserTestData.assertMatch
import munoon.bank.service.resource.user.util.FieldValidationException
import munoon.bank.service.resource.user.util.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import javax.validation.ValidationException

internal class UserServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun getById() {
        val user = userService.getById(USER_ID)
        assertMatch(user, DEFAULT_USER)
    }

    @Test
    fun getByIdNotFound() {
        assertThrows(NotFoundException::class.java) { userService.getById(999) }
    }

    @Test
    fun getAll() {
        with(userService.getAll(PageRequest.of(0, 1))) {
            assertThat(totalElements).isEqualTo(1)
            assertMatch(content, DEFAULT_USER)
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

        assertMatch(userService.getAll(PageRequest.of(0, 10)).content, DEFAULT_USER, expected)
        assertThat(passwordEncoder.matches("password", createUser.password)).isTrue()
    }

    @Test
    fun updateUser() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", emptySet())
        userService.updateUser(USER_ID, userTo)

        val expected = User(100, "NewName", "NewSurname", "test", "password", LocalDateTime.now(), emptySet())
        val actual = userService.getById(USER_ID)
        assertMatch(actual, expected)
    }

    @Test
    fun updateUserNotFound() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", emptySet())
        assertThrows(NotFoundException::class.java) { userService.updateUser(999, userTo) }
    }

    @Test
    fun updateUserPasswordAdmin() {
        userService.updateUser(USER_ID, AdminUpdateUserPasswordTo("newPassword"))

        val userPassword = userService.getById(USER_ID).password
        assertThat(passwordEncoder.matches("newPassword", userPassword)).isTrue()
    }

    @Test
    fun updateUserPasswordAdminNotFound() {
        assertThrows(NotFoundException::class.java) {
            userService.updateUser(999, AdminUpdateUserPasswordTo("newPassword"))
        }
    }

    @Test
    fun updateUserPassword() {
        val newPassword = "newPassword"
        val userTo = UpdatePasswordTo(newPassword, USER_PASSWORD)
        userService.updateUser(USER_ID, userTo)

        val actual = userService.getById(USER_ID)
        assertThat(passwordEncoder.matches(newPassword, actual.password)).isTrue()
    }

    @Test
    fun updateUserPasswordNotFound() {
        val userTo = UpdatePasswordTo("newPassword", USER_PASSWORD)
        assertThrows(NotFoundException::class.java) { userService.updateUser(999, userTo) }
    }

    @Test
    fun updateUserPasswordOldPasswordIncorrect() {
        val userTo = UpdatePasswordTo("newPassword", "incorrectPassword")
        val ex = assertThrows(FieldValidationException::class.java) { userService.updateUser(USER_ID, userTo) }
        assertThat(ex.field).isEqualTo("oldPassword")
    }

    @Test
    fun updateUserUsername() {
        val newUsername = "newUsername"
        val userTo = UpdateUsernameTo(USER_PASSWORD, newUsername)
        userService.updateUser(USER_ID, userTo)

        val actual = userService.getById(USER_ID)
        val expected = User(100, "Nikita", "Ivchenko", newUsername, "", LocalDateTime.now(), DEFAULT_USER.roles)
        assertMatch(actual, expected)
    }

    @Test
    fun updateUserUsernameNotFound() {
        val userTo = UpdateUsernameTo(USER_PASSWORD, "newUsername")
        assertThrows(NotFoundException::class.java) { userService.updateUser(999, userTo) }
    }

    @Test
    fun updateUserUsernamePasswordIncorrect() {
        val userTo = UpdateUsernameTo("newPassword", "newUsername")
        val ex = assertThrows(FieldValidationException::class.java) { userService.updateUser(USER_ID, userTo) }
        assertThat(ex.field).isEqualTo("password")
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