package munoon.bank.service.transactional.controller

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.card.CardTestData.contentJson
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.ResponseExceptionValidator.fieldError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.ws.rs.core.MediaType

internal class CardsControllerTest : AbstractTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun buyCard() {
        val result = mockMvc.perform(post("/cards/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(BuyCardTo("default", "1111", null)))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val card = JsonUtil.readFromJson(result, CardTo::class.java)
        val expected = Card(card.id, 100, "default", null, "", 0.0)
        assertMatch(cardService.getCards(100), expected)

        val actualCard = cardService.getCardById(card.id)
        assertThat(passwordEncoder.matches("1111", actualCard.pinCode)).isTrue()
    }

    @Test
    fun buyCardValidationError() {
        mockMvc.perform(post("/cards/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(BuyCardTo("abc", "11111", null)))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("type", "pinCode"))
    }

    @Test
    fun getCards() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null))
        val expected = Card(card.id, 100, "default", null, "", 0.0)

        mockMvc.perform(get("/cards")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(expected.asTo()))

        assertMatch(cardService.getCards(100), expected)
    }
}