package munoon.bank.service.transactional.controller

import munoon.bank.common.transaction.to.PaySalaryTransactionDataTo
import munoon.bank.common.transaction.to.SalaryUserTransactionInfoTo
import munoon.bank.service.transactional.AbstractWebTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.UserTransaction
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionTo
import munoon.bank.service.transactional.transaction.UserTransactionType
import munoon.bank.service.transactional.util.JsonUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.ws.rs.core.MediaType

internal class MicroserviceTransactionControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var transactionService: UserTransactionService

    @Test
    fun paySalary() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(0.0)

        val data = PaySalaryTransactionDataTo(100, 100.0)
        val result = mockMvc.perform(post("/microservices/transaction/payout/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(data)))
                .andExpect(status().isOk())
                .andReturn()

        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(85.0)

        val transaction = JsonUtil.readFromJson(result, SalaryUserTransactionInfoTo::class.java)
        val expected = UserTransaction(transaction.id, card.copy(balance = 85.0), 85.0, 100.0, 85.0, transaction.registered, UserTransactionType.SALARY, null, false)
        val actual = transactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content
        assertMatch(actual, expected)
    }

    @Test
    fun payCardService() {
        val card = cardService.createCard(AdminCreateCardTo(100, "gold", null, "1111", true))
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(0.0)

        val result = mockMvc.perform(post("/microservices/transaction/payout/card/service")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(card.id)))
            .andExpect(status().isOk())
            .andReturn()

        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(-10.0)
        val transaction = JsonUtil.readFromJson(result, UserTransactionTo::class.java)
        val expected = UserTransaction(transaction.id, card.copy(balance = -10.0), 10.0, 10.0, -10.0, transaction.registered, UserTransactionType.CARD_SERVICE, null, false)
        val actual = transactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content
        assertMatch(actual, expected)
    }
}