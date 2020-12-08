package munoon.bank.service.transactional.controller

import munoon.bank.common.error.ErrorType
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.BuyCardTo
import munoon.bank.service.transactional.card.CardDataTo
import munoon.bank.service.transactional.card.CardRepository
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.util.ResponseExceptionValidator.error
import munoon.bank.service.transactional.util.ResponseExceptionValidator.fieldError
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

internal class TransactionControllerTest : AbstractTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Test
    fun getTransactionsList() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val transaction = userTransactionService.buyCardTransaction(100, 100.0, CardDataTo(card.number!!, "1111"))
        val expected = UserTransaction(transaction.id, card.copy(balance = 900.0), 100.0, 900.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, null)

        mockMvc.perform(get("/transaction/" + card.id)
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(UserTransactionTestData.contentJsonList(expected.asTo()))
    }

    @Test
    fun getTransactionsListValidationError() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        mockMvc.perform(get("/transaction/" + card.id)
                .param("size", "99999")
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("getTransactionsList.pageable"))
    }

    @Test
    fun getTransactionsCardNotFound() {
        mockMvc.perform(get("/transaction/999")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }
}