package munoon.bank.service.resource.user.controller

import munoon.bank.common.error.ErrorType
import munoon.bank.common.user.User
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.AbstractWebTest
import munoon.bank.service.resource.user.user.*
import munoon.bank.service.resource.user.user.UserTestData.DEFAULT_USER
import munoon.bank.service.resource.user.user.UserTestData.USER_ID
import munoon.bank.service.resource.user.user.UserTestData.assertMatch
import munoon.bank.service.resource.user.user.UserTestData.contentJson
import munoon.bank.service.resource.user.user.UserTestData.contentJsonPage
import munoon.bank.service.resource.user.util.JsonUtils
import munoon.bank.service.resource.user.util.ResponseExceptionValidator
import munoon.bank.service.resource.user.util.ResponseExceptionValidator.error
import munoon.bank.service.resource.user.util.ResponseExceptionValidator.fieldError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

internal class AdminControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun getUsersList() {
        mockMvc.perform(get("/admin")
                .param("page", "0")
                .param("size", "1")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonPage(DEFAULT_USER.asTo()))

        mockMvc.perform(get("/admin")
                .param("page", "1")
                .param("size", "1")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonPage())
    }

    @Test
    fun getUsersListInvalid() {
        mockMvc.perform(get("/admin")
                .param("page", "0")
                .param("size", "30")
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("getUsersList.pageable"))
    }

    @Test
    fun getUser() {
        mockMvc.perform(get("/admin/$USER_ID")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(DEFAULT_USER.asTo()))
    }

    @Test
    fun getUserNotFound() {
        mockMvc.perform(get("/admin/999")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun createUser() {
        val userTo = AdminRegisterUserTo("New", "User", "username", "password", emptySet())

        val result = mockMvc.perform(post("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val created = JsonUtils.readFromJson(result, UserTo::class.java)

        val expected = User(created.id, "New", "User", "username", "password", created.registered, emptySet())
        assertMatch(userService.getAll(PageRequest.of(0, 10)).content, DEFAULT_USER, expected)
        assertThat(passwordEncoder.matches("password", userService.getById(created.id).password)).isTrue()
    }

    @Test
    fun createUserInvalid() {
        val userTo = AdminRegisterUserTo("", "", "", "", emptySet())

        mockMvc.perform(post("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("name", "surname", "username", "password"))
    }

    @Test
    fun updateUser() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", emptySet())
        mockMvc.perform(put("/admin/$USER_ID")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isOk())

        val expected = User(100, "NewName", "NewSurname", "test", "password", LocalDateTime.now(), emptySet())
        val actual = userService.getById(USER_ID)
        assertMatch(actual, expected)
    }

    @Test
    fun updateUserInvalid() {
        val userTo = AdminUpdateUserTo("", "", "", emptySet())
        mockMvc.perform(put("/admin/$USER_ID")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("name", "surname", "username"))
    }

    @Test
    fun updateUserNotFound() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", emptySet())
        mockMvc.perform(put("/admin/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun updateUserPassword() {
        mockMvc.perform(put("/admin/$USER_ID/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(AdminUpdateUserPasswordTo("newPassword")))
                .with(authUser()))
                .andExpect(status().isOk())

        val userPassword = userService.getById(USER_ID).password
        assertThat(passwordEncoder.matches("newPassword", userPassword)).isTrue()
    }

    @Test
    fun updateUserPasswordInvalid() {
        mockMvc.perform(put("/admin/$USER_ID/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(AdminUpdateUserPasswordTo("")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("password"))
    }

    @Test
    fun updateUserPasswordNotFound() {
        mockMvc.perform(put("/admin/999/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(AdminUpdateUserPasswordTo("newPassword")))
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun deleteUser() {
        with (userService.getAll(PageRequest.of(0, 1))) {
            assertThat(totalElements).isEqualTo(1)
            assertThat(content.size).isEqualTo(1)
        }

        mockMvc.perform(delete("/admin/$USER_ID")
                .with(authUser()))
                .andExpect(status().isNoContent())

        with (userService.getAll(PageRequest.of(0, 1))) {
            assertThat(totalElements).isEqualTo(0)
            assertThat(content.size).isEqualTo(0)
        }
    }

    @Test
    fun deleteUserNotFound() {
        mockMvc.perform(delete("/admin/999")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }
}