package munoon.bank.service.transactional.controller

import munoon.bank.common.error.ErrorType
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.card.CardTestData.contentJsonList
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.ResponseExceptionValidator.error
import munoon.bank.service.transactional.util.ResponseExceptionValidator.fieldError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
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
        val expected = Card(card.id, 100, "default", null, "", 0.0, LocalDateTime.now())
        assertMatch(cardService.getCardsByUserId(100), expected)

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
        val expected = Card(card.id, 100, "default", null, "", 0.0, LocalDateTime.now())

        mockMvc.perform(get("/cards")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonList(expected.asTo()))

        assertMatch(cardService.getCardsByUserId(100), expected)
    }

    @Test
    fun updateCardPinCode() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111"))

        mockMvc.perform(post("/cards/${card.id}/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(UserUpdateCardPinCode("1111", "2222")))
                .with(authUser()))
                .andExpect(status().isNoContent())

        val actual = cardService.getCardById(card.id!!)
        assertThat(passwordEncoder.matches("2222", actual.pinCode)).isTrue()
    }

    @Test
    fun updateCardPinCodeValidationError() {
        mockMvc.perform(post("/cards/1111/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(UserUpdateCardPinCode("1", "1")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("oldPinCode", "newPinCode"))
    }

    @Test
    fun updateCardPinCodeCardNotFound() {
        mockMvc.perform(post("/cards/abc/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(UserUpdateCardPinCode("1111", "2222")))
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun updateCardPinCodeOldPinCodeIncorrect() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111"))

        mockMvc.perform(post("/cards/${card.id}/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(UserUpdateCardPinCode("2222", "2222")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("oldPinCode"))
    }
}