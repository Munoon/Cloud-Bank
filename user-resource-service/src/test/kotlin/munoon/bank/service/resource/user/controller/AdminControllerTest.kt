package munoon.bank.service.resource.user.controller

import munoon.bank.common.card.CardTo
import munoon.bank.common.error.ErrorType
import munoon.bank.common.user.FullUserTo
import munoon.bank.common.user.User
import munoon.bank.service.resource.user.AbstractWebTest
import munoon.bank.service.resource.user.user.*
import munoon.bank.service.resource.user.user.UserTestData.DEFAULT_USER
import munoon.bank.service.resource.user.user.UserTestData.USER_CLASS
import munoon.bank.service.resource.user.user.UserTestData.USER_ID
import munoon.bank.service.resource.user.user.UserTestData.assertMatch
import munoon.bank.service.resource.user.user.UserTestData.contentJson
import munoon.bank.service.resource.user.user.UserTestData.contentJsonPage
import munoon.bank.service.resource.user.util.JsonUtils
import munoon.bank.service.resource.user.util.JsonUtils.contentJsonList
import munoon.bank.service.resource.user.util.JsonUtils.emptyJsonPage
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
        val userTo = AdminRegisterUserTo("New", "User", "username", "password", "9", 100.0, emptySet())
        val createdUser = userService.createUser(userTo)

        mockMvc.perform(get("/admin")
                .param("page", "0")
                .param("size", "1")
                .param("class", USER_CLASS)
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonPage(DEFAULT_USER.asFullTo()))

        mockMvc.perform(get("/admin")
                .param("page", "1")
                .param("size", "1")
                .param("class", USER_CLASS)
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(emptyJsonPage())

        mockMvc.perform(get("/admin")
                .param("page", "0")
                .param("size", "1")
                .param("class", "9")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonPage(createdUser.asFullTo()))
    }

    @Test
    fun getUsersListInvalid() {
        mockMvc.perform(get("/admin")
                .param("page", "0")
                .param("size", "30")
                .param("class", "abc")
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("getUsersList.pageable", "getUsersList.clazz"))
    }

    @Test
    fun getUser() {
        val expectedCard = CardTo("CARD_ID", 100, "default", "123456789012", 0.0, true, true, LocalDateTime.of(2020, 12, 6, 0, 10))
        val expectedCards = listOf(expectedCard)

        mockMvc.perform(get("/admin/$USER_ID")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(DEFAULT_USER.asFullTo(expectedCards)))
    }

    @Test
    fun getUserNotFound() {
        mockMvc.perform(get("/admin/999")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun findUsers() {
        fun find(query: String) = mockMvc.perform(get("/admin/find")
                .param("page", "0")
                .param("size", "1")
                .param("query", query)
                .with(authUser()))
                .andExpect(status().isOk())

        find("kit").andExpect(contentJsonPage(DEFAULT_USER.asFullTo()))
        find("che").andExpect(contentJsonPage(DEFAULT_USER.asFullTo()))
        find("${DEFAULT_USER.name} ${DEFAULT_USER.surname}").andExpect(contentJsonPage(DEFAULT_USER.asFullTo()))
        find("${DEFAULT_USER.surname} ${DEFAULT_USER.name}").andExpect(contentJsonPage(DEFAULT_USER.asFullTo()))
    }

    @Test
    fun findUsersInvalid() {
        mockMvc.perform(get("/admin/find")
                .param("page", "0")
                .param("size", "30")
                .param("query", "q")
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("findUser.pageable", "findUser.query"))
    }

    @Test
    fun getClassesList() {
        mockMvc.perform(get("/admin/classes")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonList("5", "6", "7", "8", "9", "10", "11"))
    }

    @Test
    fun createUser() {
        val userTo = AdminRegisterUserTo("New", "User", "username", "password", "10", 100.0, emptySet())

        val result = mockMvc.perform(post("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isCreated())
                .andReturn()

        val created = JsonUtils.readFromJson(result, FullUserTo::class.java)

        val expected = User(created.id, "New", "User", "username", "password", "10", 100.0, created.registered, emptySet())
        assertMatch(userService.getAll(PageRequest.of(0, 10), USER_CLASS).content, DEFAULT_USER, expected)
        assertThat(passwordEncoder.matches("password", userService.getById(created.id).password)).isTrue()
    }

    @Test
    fun createUserInvalid() {
        val userTo = AdminRegisterUserTo("", "", "", "", "invalid-class", 100.0, emptySet())

        mockMvc.perform(post("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("name", "surname", "username", "password", "clazz"))
    }

    @Test
    fun updateUser() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", "10", 100.0, emptySet())
        mockMvc.perform(put("/admin/$USER_ID")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isOk())

        val expected = User(100, "NewName", "NewSurname", "test", "password", "10", 100.0, LocalDateTime.now(), emptySet())
        val actual = userService.getById(USER_ID)
        assertMatch(actual, expected)
    }

    @Test
    fun updateUserInvalid() {
        val userTo = AdminUpdateUserTo("", "", "", "invalid-class", 100.0, emptySet())
        mockMvc.perform(put("/admin/$USER_ID")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("name", "surname", "username", "clazz"))
    }

    @Test
    fun updateUserNotFound() {
        val userTo = AdminUpdateUserTo("NewName", "NewSurname", "test", "10", 100.0, emptySet())
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
        with (userService.getAll(PageRequest.of(0, 1), USER_CLASS)) {
            assertThat(totalElements).isEqualTo(1)
            assertThat(content.size).isEqualTo(1)
        }

        mockMvc.perform(delete("/admin/$USER_ID")
                .with(authUser()))
                .andExpect(status().isNoContent())

        with (userService.getAll(PageRequest.of(0, 1), USER_CLASS)) {
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