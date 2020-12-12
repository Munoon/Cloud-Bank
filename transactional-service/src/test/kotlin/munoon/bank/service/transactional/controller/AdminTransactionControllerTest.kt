package munoon.bank.service.transactional.controller

import munoon.bank.common.error.ErrorType
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.card.CardTestData
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.ResponseExceptionValidator
import munoon.bank.service.transactional.util.ResponseExceptionValidator.error
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import javax.ws.rs.core.MediaType

internal class AdminTransactionControllerTest : AbstractTest() {
    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cardService: CardService

    @Test
    fun makeAward() {
        val card = cardService.createCard(AdminCreateCardTo(101, "default", "111111111111", "1111", true))

        val result = mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo(card.number!!, 10.0, FineAwardType.AWARD, null)))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val transaction = UserTransactionTestData.readFromJson(JsonUtil.getContent(result))

        CardTestData.assertMatch(cardService.getCardsByUserId(101), card.copy(balance = 10.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 10.0), 10.0, 10.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(100, null))
        val actual = userTransactionService.getTransactions(card.id!!, 101, PageRequest.of(0, 10))
        UserTransactionTestData.assertMatch(actual.content, expected)
    }

    @Test
    fun makeFine() {
        val card = cardService.createCard(AdminCreateCardTo(101, "default", "111111111111", "1111", true)).let {
            cardService.plusMoney(it, 100.0)
        }

        val result = mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo(card.number!!, 10.0, FineAwardType.FINE, "message")))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val transaction = UserTransactionTestData.readFromJson(JsonUtil.getContent(result))

        CardTestData.assertMatch(cardService.getCardsByUserId(101), card.copy(balance = 90.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 90.0), 10.0, 90.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(100, "message"))
        val actual = userTransactionService.getTransactions(card.id!!, 101, PageRequest.of(0, 10))
        UserTransactionTestData.assertMatch(actual.content, expected)
    }

    @Test
    fun makeFineOrAwardCardNotFound() {
        mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo("abc", 10.0, FineAwardType.FINE, "message")))
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun makeFineOrAwardCardNotActive() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", false))
        mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo(card.number!!, 10.0, FineAwardType.FINE, "message")))
                .with(authUser()))
                .andExpect(status().isForbidden())
                .andExpect(error(ErrorType.ACCESS_DENIED))
    }
}