package munoon.bank.service.resource.user.controller

import munoon.bank.common.user.User
import munoon.bank.service.resource.user.AbstractWebTest
import munoon.bank.service.resource.user.user.*
import munoon.bank.service.resource.user.user.UserTestData.assertMatch
import munoon.bank.service.resource.user.user.UserTestData.contentJson
import munoon.bank.service.resource.user.util.JsonUtils
import munoon.bank.service.resource.user.util.ResponseExceptionValidator.fieldError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
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
        val userTo = UpdatePasswordTo(newPassword, UserTestData.USER_PASSWORD)

        mockMvc.perform(post("/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isNoContent())

        val actual = userService.getById(UserTestData.USER_ID)
        assertThat(passwordEncoder.matches(newPassword, actual.password)).isTrue()
    }

    @Test
    fun updatePasswordOldPasswordIncorrect() {
        mockMvc.perform(post("/profile/password")
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
        val userTo = UpdateUsernameTo(UserTestData.USER_PASSWORD, newUsername)
        val expected = User(100, "Nikita", "Ivchenko", newUsername, "", LocalDateTime.now(), UserTestData.DEFAULT_USER.roles)

        mockMvc.perform(post("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(userTo))
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(expected.asTo()))

        val actual = userService.getById(UserTestData.USER_ID)
        assertMatch(actual, expected)
    }

    @Test
    fun updateUsernamePasswordIncorrect() {
        mockMvc.perform(post("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(UpdateUsernameTo("incorrectPassword", "newUsername")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("password"))
    }
}