package munoon.bank.service.transactional.controller

import munoon.bank.service.transactional.AbstractWebTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

internal class MicroserviceCardControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Test
    fun deactivateByUser() {
        val (cardId) = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))

        mockMvc.perform(post("/microservices/card/deactivate")
                .param("userId", "100"))
                .andExpect(status().isNoContent())

        val expected = Card(cardId, 100, "default", null, "", 0.0, false, LocalDateTime.now())
        assertMatch(cardService.getCardsByUserId(100), expected)
    }
}