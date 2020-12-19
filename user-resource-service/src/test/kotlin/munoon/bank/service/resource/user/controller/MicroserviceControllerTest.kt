package munoon.bank.service.resource.user.controller

import munoon.bank.service.resource.user.AbstractWebTest
import munoon.bank.service.resource.user.user.UserTestData
import munoon.bank.service.resource.user.user.UserTestData.contentJsonList
import munoon.bank.service.resource.user.user.asTo
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class MicroserviceControllerTest : AbstractWebTest() {
    @Test
    fun getUsersById() {
        mockMvc.perform(get("/microservices/users")
                .param("ids", "100,101"))
                .andExpect(status().isOk())
                .andExpect(contentJsonList(UserTestData.DEFAULT_USER.asTo()))
    }
}