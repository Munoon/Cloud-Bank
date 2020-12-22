package munoon.bank.service.transactional.controller

import munoon.bank.common.error.ErrorType
import munoon.bank.common.user.UserRoles
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.AbstractWebTest
import munoon.bank.service.transactional.card.BuyCardTo
import munoon.bank.service.transactional.card.CardRepository
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.transaction.UserTransactionTestData.contentJsonList
import munoon.bank.service.transactional.user.UserService
import munoon.bank.service.transactional.user.UserTestData
import munoon.bank.service.transactional.util.ResponseExceptionValidator.error
import munoon.bank.service.transactional.util.ResponseExceptionValidator.fieldError
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as mockWhen

internal class

UserTransactionControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Autowired
    private lateinit var userTransactionMapper: UserTransactionMapper

    @MockBean
    private lateinit var userService: UserService

    @Test
    fun getTransactionsList() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val usersMap = mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to UserTo(101, "test", "test", "username", "10", LocalDateTime.now(), setOf(UserRoles.ROLE_ADMIN))
        )
        mockWhen(userService.getUsersById(setOf(100, 101))).thenReturn(usersMap)

        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, "abc"))
        val expected = UserTransaction(transaction.id, card.copy(balance = 1085.0), 85.0, 100.0, 1085.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, "abc"), false)

        mockMvc.perform(get("/transaction/" + card.id)
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonList(userTransactionMapper.asTo(expected, usersMap)))
    }

    @Test
    fun getTransactionsListValidationError() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        mockWhen(userService.getUsersById(setOf(100, 101))).thenReturn(mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to UserTo(101, "test", "test", "username", "10", LocalDateTime.now(), setOf(UserRoles.ROLE_ADMIN))
        ))

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