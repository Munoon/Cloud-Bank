package munoon.bank.service.transactional.controller

import munoon.bank.common.error.ErrorType
import munoon.bank.common.user.UserRoles
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.AbstractWebTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.transaction.UserTransactionTestData.contentJsonList
import munoon.bank.service.transactional.user.UserService
import munoon.bank.service.transactional.user.UserTestData
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.ResponseExceptionValidator.error
import munoon.bank.service.transactional.util.ResponseExceptionValidator.fieldError
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import javax.ws.rs.core.MediaType
import org.mockito.Mockito.`when` as mockWhen

internal class AdminTransactionControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cardService: CardService

    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Autowired
    private lateinit var userTransactionMapper: UserTransactionMapper

    @Test
    fun makeAward() {
        val card = cardService.createCard(AdminCreateCardTo(101, "default", "111111111111", "1111", true))

        val user101 = UserTo(101, "test", "test", "username", "10", LocalDateTime.now(), setOf(UserRoles.ROLE_ADMIN))

        mockWhen(userService.getUsersById(setOf(100, 101))).thenReturn(mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to user101
        ))

        val result = mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null)))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val transaction = UserTransactionTestData.readFromJson(JsonUtil.getContent(result))

        UserTestData.assertMatch((transaction.info as AwardUserTransactionInfoTo).user!!, UserTestData.DEFAULT_USER_TO)
        UserTestData.assertMatch(transaction.card.owner!!, user101)

        CardTestData.assertMatch(cardService.getCardsByUserId(101), card.copy(balance = 85.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 85.0), 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(100, null))
        val actual = userTransactionService.getTransactions(card.id!!, 101, PageRequest.of(0, 10))
        UserTransactionTestData.assertMatch(actual.content, expected)
    }

    @Test
    fun makeFine() {
        val card = cardService.createCard(AdminCreateCardTo(101, "default", "111111111111", "1111", true)).let {
            cardService.plusMoney(it, 100.0)
        }

        val user101 = UserTo(101, "test", "test", "username", "10", LocalDateTime.now(), setOf(UserRoles.ROLE_ADMIN))

        mockWhen(userService.getUsersById(setOf(100, 101))).thenReturn(mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to user101
        ))

        val result = mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo(card.number!!, 100.0, FineAwardType.FINE, "message")))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val transaction = UserTransactionTestData.readFromJson(JsonUtil.getContent(result))

        UserTestData.assertMatch((transaction.info as FineUserTransactionInfoTo).user!!, UserTestData.DEFAULT_USER_TO)
        UserTestData.assertMatch(transaction.card.owner!!, user101)

        CardTestData.assertMatch(cardService.getCardsByUserId(101), card.copy(balance = -15.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = -15.0), 115.0, 100.0, -15.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(100, "message"))
        val actual = userTransactionService.getTransactions(card.id!!, 101, PageRequest.of(0, 10))
        UserTransactionTestData.assertMatch(actual.content, expected)
    }

    @Test
    fun makeFineOrAwardCardNotFound() {
        mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo("111111111111", 10.0, FineAwardType.FINE, "message")))
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

    @Test
    fun makeFineOrAwardValidationError() {
        mockMvc.perform(post("/admin/transaction/fine-award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(FineAwardDataTo("11111111111", 10.0001, FineAwardType.FINE, "message")))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("card", "count"))
    }

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
        val expected = UserTransaction(transaction.id, card.copy(balance = 1085.0), 85.0, 100.0, 1085.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, "abc"))

        mockMvc.perform(get("/admin/transaction/${card.id}")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentJsonList(userTransactionMapper.asTo(expected)))
    }

    @Test
    fun getTransactionsListNotValid() {
        mockMvc.perform(get("/admin/transaction/abc")
                .param("size", "100")
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("getCardTransactions.pageable"))
    }
}