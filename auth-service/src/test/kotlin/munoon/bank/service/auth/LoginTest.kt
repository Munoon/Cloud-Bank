package munoon.bank.service.auth

import munoon.bank.service.auth.JsonTestUtils.authInfoJson
import munoon.bank.service.auth.authentication.FailAuthenticationInfo
import munoon.bank.service.auth.authentication.SuccessAuthenticationInfo
import munoon.bank.service.auth.user.UserTestData.USER_PASSWORD
import munoon.bank.service.auth.user.UserTestData.USER_USERNAME
import org.junit.jupiter.api.Test
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.ws.rs.core.MediaType

internal class LoginTest : AbstractWebTest() {
    @Test
    fun testSuccess() {
        mockMvc.perform(post("/login")
                .param("username", USER_USERNAME)
                .param("password", USER_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(authenticated().withUsername(USER_USERNAME))
                .andExpect(authInfoJson(SuccessAuthenticationInfo("/")))
    }

    @Test
    fun testUnsuccessful() {
        val authInfo = FailAuthenticationInfo("bad_credentials", "Bad credentials")
        mockMvc.perform(post("/login")
                .param("username", USER_USERNAME)
                .param("password", "INCORRECT_PASS")
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(unauthenticated())
                .andExpect(authInfoJson(authInfo))
    }
}