package munoon.bank.service.transactional.controller

import munoon.bank.common.error.ErrorType
import munoon.bank.service.transactional.AbstractWebTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.card.CardTestData.contentJson
import munoon.bank.service.transactional.card.CardTestData.contentJsonList
import munoon.bank.service.transactional.card.CardTestData.contentJsonWithOwner
import munoon.bank.service.transactional.user.UserTestData
import munoon.bank.service.transactional.user.UserTestData.assertMatch
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.ResponseExceptionValidator.error
import munoon.bank.service.transactional.util.ResponseExceptionValidator.fieldError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.ws.rs.core.MediaType

internal class AdminCardsControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var cardMapper: CardMapper

    @Test
    fun getCardsByUser() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val expected = Card(card.id, 100, "default", "111111111111", "", 0.0, active = true, primary = true, card.registered)

        mockMvc.perform(get("/admin/cards/100")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonList(cardMapper.asTo(expected)))
    }

    @Test
    fun getCardByNumber() {
        val cardNumber = "123456789012"
        val card = cardService.createCard(AdminCreateCardTo(100, "default", cardNumber, "1111", true))
        val expected = Card(card.id, 100, "default", cardNumber, "", 0.0, active = true, primary = true, card.registered)

        mockMvc.perform(get("/admin/cards/number/$cardNumber")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonWithOwner(cardMapper.asToWithUser(expected)))
    }

    @Test
    fun getCardByNumberNotFound() {
        mockMvc.perform(get("/admin/cards/number/123456789012")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun getCardByNumberValidationError() {
        mockMvc.perform(get("/admin/cards/number/11")
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("getCardByNumber.cardNumber"))
    }

    @Test
    fun getCardById() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val expected = Card(card.id, 100, "default", "111111111111", "", 0.0, active = true, primary = true, card.registered)

        mockMvc.perform(get("/admin/cards/100/${card.id}")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJson(cardMapper.asTo(expected)))
    }

    @Test
    fun getCardByIdNotFound() {
        mockMvc.perform(get("/admin/cards/100/abc")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun getCardByIdOtherOwner() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))

        mockMvc.perform(get("/admin/cards/101/${card.id}")
                .with(authUser()))
                .andExpect(status().isForbidden())
                .andExpect(error(ErrorType.ACCESS_DENIED))
    }

    @Test
    fun updateCard() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))

        mockMvc.perform(put("/admin/cards/100/${card.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardTo(101, "gold", "222222222222", false)))
                .with(authUser()))
                .andExpect(status().isOk())

        val expected = Card(card.id, 101, "gold", "222222222222", "", 0.0, active = false, primary = true, card.registered)
        assertMatch(cardService.getCardsByUserId(101), expected)
    }

    @Test
    fun updateCardNotFound() {
        mockMvc.perform(put("/admin/cards/100/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardTo(101, "gold", "222222222222", true)))
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun updateCardOtherOwner() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        mockMvc.perform(put("/admin/cards/101/${card.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardTo(101, "gold", "222222222222", true)))
                .with(authUser()))
                .andExpect(status().isForbidden())
                .andExpect(error(ErrorType.ACCESS_DENIED))
    }

    @Test
    fun updateCardValidationException() {
        mockMvc.perform(put("/admin/cards/100/1111")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardTo(101, "", "", true)))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("type", "number"))
    }

    @Test
    fun updateCardPinCode() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        mockMvc.perform(put("/admin/cards/100/${card.id}/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardPinCode("2222")))
                .with(authUser()))
                .andExpect(status().isNoContent())
        assertThat(passwordEncoder.matches("2222", cardService.getCardById(card.id!!).pinCode)).isTrue()
    }

    @Test
    fun updateCardPinCodeCardNotFound() {
        mockMvc.perform(put("/admin/cards/100/abc/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardPinCode("2222")))
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun updateCardPinCodeOtherOwner() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        mockMvc.perform(put("/admin/cards/101/${card.id}/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardPinCode("2222")))
                .with(authUser()))
                .andExpect(status().isForbidden())
                .andExpect(error(ErrorType.ACCESS_DENIED))
    }

    @Test
    fun updateCardPinCodeValidationException() {
        mockMvc.perform(put("/admin/cards/100/1111/pinCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminUpdateCardPinCode("222")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("pinCode"))
    }

    @Test
    fun createCard() {
        val result = mockMvc.perform(post("/admin/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminCreateCardTo(100, "default", "111111111111", "1111", true)))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val card = JsonUtil.readFromJson(result, CardToWithOwner::class.java)
        val expected = Card(card.id, 100, "default", "111111111111", "", 0.0, active = true, primary = true, card.registered)
        assertMatch(cardService.getCardsByUserId(100), expected)
        assertThat(passwordEncoder.matches("1111", cardService.getCardById(card.id).pinCode)).isTrue()
        assertMatch(card.owner!!, UserTestData.DEFAULT_USER_TO)
    }

    @Test
    fun createCardValidationException() {
        mockMvc.perform(post("/admin/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(AdminCreateCardTo(100, "", "", "", true)))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("type", "number", "pinCode"))
    }

    @Test
    fun changePrimaryCard() {
        val card1 = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val card2 = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))

        assertThat(card1.primary).isTrue()
        assertThat(card2.primary).isFalse()

        mockMvc.perform(put("/admin/cards/100/${card2.id!!}/primary")
                .with(authUser()))
                .andExpect(status().isNoContent())

        assertMatch(cardService.getCardById(card1.id!!), card1.copy(primary = false))
        assertMatch(cardService.getCardById(card2.id!!), card2.copy(primary = true))
    }
}