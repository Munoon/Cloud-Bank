package munoon.bank.service.auth

import munoon.bank.service.auth.user.UserTestData.USER_PASSWORD
import munoon.bank.service.auth.user.UserTestData.USER_USERNAME
import org.junit.jupiter.api.Test
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

internal class LoginTest : AbstractWebTest() {
    @Test
    fun testSuccess() {
        mockMvc.perform(post("/login")
                .param("username", USER_USERNAME)
                .param("password", USER_PASSWORD)
                .with(csrf()))
                .andExpect(authenticated().withUsername(USER_USERNAME))
    }

    @Test
    fun testUnsuccessful() {
        mockMvc.perform(post("/login")
                .param("username", USER_USERNAME)
                .param("password", "INCORRECT_PASS")
                .with(csrf()))
                .andExpect(unauthenticated())
    }
}