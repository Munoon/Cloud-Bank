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
import org.assertj.core.api.Assertions
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
                .andExpect(contentJsonList(userTransactionMapper.asTo(expected, usersMap, emptyMap())))
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

    @Test
    fun translateMoney() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        val receiverCard = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        cardService.plusMoney(senderCard, 200.0)

        val translateMoneyData = TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111"))
        val result = mockMvc.perform(post("/transaction/translate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(translateMoneyData))
                .with(authUser()))
                .andExpect(status().isOk())
                .andReturn()

        val transaction = UserTransactionTestData.readFromJson(JsonUtil.getContent(result))

        Assertions.assertThat(cardService.getCardById(senderCard.id!!).balance).isEqualTo(85.0)
        Assertions.assertThat(cardService.getCardById(receiverCard.id!!).balance).isEqualTo(100.0)

        val receiverTransactionId = (transaction.info as TranslateUserTransactionInfoTo).receiveTransaction.id!!
        val expectedSenderTransaction = UserTransaction(
                id = transaction.id,
                card = senderCard.copy(balance = 85.0),
                price = 115.0,
                actualPrice = 100.0,
                leftBalance = 85.0,
                registered = transaction.registered,
                type = UserTransactionType.TRANSLATE_MONEY,
                info = TranslateUserTransactionInfo(receiverTransactionId, 100, "test"),
                canceled = false
        )

        val expectedReceiverTransaction = UserTransaction(
                id = receiverTransactionId,
                card = receiverCard.copy(balance = 100.0),
                price = 100.0,
                actualPrice = 100.0,
                leftBalance = 100.0,
                registered = transaction.registered,
                type = UserTransactionType.RECEIVE_MONEY,
                info = ReceiveUserTransactionInfo(transaction.id!!, 100, "test"),
                canceled = false
        )

        val request = PageRequest.of(0, 10)
        UserTransactionTestData.assertMatch(userTransactionService.getTransactions(senderCard.id!!, 100, request).content, expectedSenderTransaction)
        UserTransactionTestData.assertMatch(userTransactionService.getTransactions(receiverCard.id!!, 100, request).content, expectedReceiverTransaction)
    }

    @Test
    fun translateMoneyValidationError() {
        val translateMoneyData = TranslateMoneyDataTo("1111", 100.00009, null, CardDataTo("1111", "1"))
        mockMvc.perform(post("/transaction/translate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(translateMoneyData))
                .with(authUser()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("receiver", "count", "cardData.card", "cardData.pinCode"))
    }
}