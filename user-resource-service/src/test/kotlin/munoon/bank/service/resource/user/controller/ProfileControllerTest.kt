package munoon.bank.service.resource.user.controller

import munoon.bank.service.resource.user.AbstractWebTest
import munoon.bank.service.resource.user.user.UserTestData
import munoon.bank.service.resource.user.user.UserTestData.contentJson
import munoon.bank.service.resource.user.user.asTo
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class ProfileControllerTest : AbstractWebTest() {
    @Test
    fun getProfile() {
        mockMvc.perform(get("/profile")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(UserTestData.DEFAULT_USER.asTo()))
    }
}