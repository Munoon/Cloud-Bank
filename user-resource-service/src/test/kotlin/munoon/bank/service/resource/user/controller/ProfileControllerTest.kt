package munoon.bank.service.resource.user.controller

import munoon.bank.common.user.User
import munoon.bank.service.resource.user.AbstractWebTest
import munoon.bank.service.resource.user.user.*
import munoon.bank.service.resource.user.user.UserTestData.USER_PASSWORD
import munoon.bank.service.resource.user.user.UserTestData.assertMatch
import munoon.bank.service.resource.user.user.UserTestData.contentJson
import munoon.bank.service.resource.user.util.JsonUtils
import munoon.bank.service.resource.user.util.ResponseExceptionValidator.fieldError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.ws.rs.core.MediaType

internal class ProfileControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun getProfile() {
        mockMvc.perform(get("/profile")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(UserTestData.DEFAULT_USER.asTo()))
    }

    @Test
    fun updatePassword() {
        val newPassword = "newPassword"
        val userTo = UpdatePasswordTo(newPassword, USER_PASSWORD)

        mockMvc.perform(put("/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isNoContent())

        val actual = userService.getById(UserTestData.USER_ID)
        assertThat(passwordEncoder.matches(newPassword, actual.password)).isTrue()
    }

    @Test
    fun updatePasswordInvalid() {
        mockMvc.perform(put("/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(UpdatePasswordTo("", USER_PASSWORD)))
                .with(authUser()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("newPassword"))
    }

    @Test
    fun updatePasswordOldPasswordIncorrect() {
        mockMvc.perform(put("/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(UpdatePasswordTo("newPassword", "incorrectPassword")))
                .with(authUser()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("oldPassword"))
    }

    @Test
    fun updateUsername() {
        val newUsername = "newUsername"
        val userTo = UpdateUsernameTo(USER_PASSWORD, newUsername)
        val expected = User(UserTestData.DEFAULT_USER).apply { username = newUsername }

        mockMvc.perform(put("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(expected.asTo()))

        val actual = userService.getById(UserTestData.USER_ID)
        assertMatch(actual, expected)
    }

    @Test
    fun updateUsernameInvalid() {
        mockMvc.perform(put("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(UpdateUsernameTo(USER_PASSWORD, "")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("newUsername"))
    }

    @Test
    fun updateUsernamePasswordIncorrect() {
        mockMvc.perform(put("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(UpdateUsernameTo("incorrectPassword", "newUsername")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("password"))
    }
}