package munoon.bank.service.transactional.transaction

import munoon.bank.common.transaction.to.SalaryUserTransactionInfoTo
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardMapper
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatchTo
import munoon.bank.service.transactional.user.UserService
import munoon.bank.service.transactional.user.UserTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as mockWhen

internal class UserTransactionMapperTest : AbstractTest() {
    private val user101 = UserTo(101, "test", "test", "test", "10", LocalDateTime.now(), emptySet())

    @Autowired
    private lateinit var cardMapper: CardMapper

    @Autowired
    private lateinit var userTransactionMapper: UserTransactionMapper

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var userTransactionService: UserTransactionService

    @BeforeEach
    fun usersSetup() {
        mockWhen(userService.getUsersById(setOf(100, 101))).thenReturn(mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to user101
        ))

        mockWhen(userService.getUsersById(setOf(100)))
                .thenReturn(mapOf(100 to UserTestData.DEFAULT_USER_TO))
    }

    @Test
    fun asToWithUnSafeInfo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asToWithUnSafeInfo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun asToWithUnSafeInfoCustomUsers() {
        val users = mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to user101
        )

        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asToWithUnSafeInfo(transaction, users, emptyMap()), expected)

        verify(userService, times(0)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun asToWithUnSafeInfoPage() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)

        val transaction2 = UserTransaction("321", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, "test2"), true)
        val expected2 = UserTransactionTo("321", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfoTo(user101, "test2"), true)

        val expectedPage = PageImpl(listOf(expected, expected2))
        val actualPage = userTransactionMapper.asToWithUnSafeInfo(PageImpl(listOf(transaction, transaction2)))
        assertMatchTo(actualPage.content, expectedPage.content)

        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun asToIgnoreInfo() {
        val users = mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to user101
        )

        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, null, true)
        assertMatch(userTransactionMapper.asToIgnoreInfo(transaction, users), expected)

        verify(userService, times(0)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun asToWithSafeInfo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val receiveTransaction = UserTransaction("456", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("RECEIVE_ID", 100, "test"), false)
        mockWhen(userTransactionService.getAll(anySet())).thenReturn(mapOf("TRANSLATE_ID" to receiveTransaction))

        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, SafeTranslateUserTransactionInfoTo(userTransactionMapper.asSafeToIgnoreInfo(receiveTransaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), "test"), true)
        assertMatch(userTransactionMapper.asToWithSafeInfo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(1)).getAll(anySet())
    }

    @Test
    fun asToWithSafeInfoPage() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val receiveTransaction = UserTransaction("456", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("RECEIVE_ID", 100, "test"), false)
        mockWhen(userTransactionService.getAll(anySet())).thenReturn(mapOf("TRANSLATE_ID" to receiveTransaction))

        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, SafeTranslateUserTransactionInfoTo(userTransactionMapper.asSafeToIgnoreInfo(receiveTransaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), "test"), true)

        val page = PageImpl(listOf(transaction))
        assertMatch(userTransactionMapper.asToWithSafeInfo(page).content, expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(1)).getAll(anySet())
    }

    @Test
    fun asToWithSafeInfoFull() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val receiveTransaction = UserTransaction("456", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("RECEIVE_ID", 100, "test"), false)

        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, SafeTranslateUserTransactionInfoTo(userTransactionMapper.asSafeToIgnoreInfo(receiveTransaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), "test"), true)
        assertMatch(userTransactionMapper.asToWithSafeInfo(transaction, mapOf(100 to UserTestData.DEFAULT_USER_TO), mapOf("TRANSLATE_ID" to receiveTransaction)), expected)
        verify(userService, times(0)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun asSafeToIgnoreInfo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        val expected = SafeUserTransactionTo("123", cardMapper.asSafeTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, null, true)
        assertMatch(userTransactionMapper.asSafeToIgnoreInfo(transaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), expected)
        verify(userService, times(0)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun buyCardAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, BuyCardUserTransactionInfoTo(cardMapper.asTo(card)), true)
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, BuyCardUserTransactionInfo(card), true)
        assertMatch(userTransactionMapper.asToWithUnSafeInfo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun awardAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, AwardUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, AwardUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asToWithUnSafeInfo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun fineAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asToWithUnSafeInfo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(0)).getAll(anySet())
    }

    @Test
    fun translateAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val receiveTransaction = UserTransaction("456", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("RECEIVE_ID", 100, "test"), false)
        mockWhen(userTransactionService.getAll(anySet())).thenReturn(mapOf("TRANSLATE_ID" to receiveTransaction))

        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfoTo(userTransactionMapper.asToIgnoreInfo(receiveTransaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), "test"), true)
        assertMatch(userTransactionMapper.asToWithUnSafeInfo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(1)).getAll(anySet())
    }

    @Test
    fun receiveAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val translateTransaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        mockWhen(userTransactionService.getAll(anySet())).thenReturn(mapOf("RECEIVE_ID" to translateTransaction))

        val transaction = UserTransaction("456", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("RECEIVE_ID", 100, "test"), false)
        val expected = UserTransactionTo("456", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfoTo(userTransactionMapper.asToIgnoreInfo(translateTransaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), "test"), false)
        assertMatch(userTransactionMapper.asToWithUnSafeInfo(transaction), expected)
        verify(userTransactionService, times(1)).getAll(anySet())
    }

    @Test
    fun translateAsSafeTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val receiveTransaction = UserTransaction("456", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("RECEIVE_ID", 100, "test"), false)
        mockWhen(userTransactionService.getAll(anySet())).thenReturn(mapOf("TRANSLATE_ID" to receiveTransaction))

        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, SafeTranslateUserTransactionInfoTo(userTransactionMapper.asSafeToIgnoreInfo(receiveTransaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), "test"), true)
        assertMatch(userTransactionMapper.asToWithSafeInfo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
        verify(userTransactionService, times(1)).getAll(anySet())
    }

    @Test
    fun receiveAsSafeTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val translateTransaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("TRANSLATE_ID", 100, "test"), true)
        mockWhen(userTransactionService.getAll(anySet())).thenReturn(mapOf("RECEIVE_ID" to translateTransaction))

        val transaction = UserTransaction("456", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("RECEIVE_ID", 100, "test"), false)
        val expected = UserTransactionTo("456", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, SafeReceiveUserTransactionInfoTo(userTransactionMapper.asSafeToIgnoreInfo(translateTransaction, mapOf(100 to UserTestData.DEFAULT_USER_TO)), "test"), false)
        assertMatch(userTransactionMapper.asToWithSafeInfo(transaction), expected)
        verify(userTransactionService, times(1)).getAll(anySet())
    }

    @Test
    fun asPaySalaryTo() {
        val card = Card("CARD_ID", 100, "default", null, "1111", 80.0, active = true, primary = true, LocalDateTime.now())
        val transaction = UserTransaction("TRANS_ID", card, 80.0, 100.0, 80.0, LocalDateTime.now(), UserTransactionType.SALARY, null, false)
        val expected = SalaryUserTransactionInfoTo("TRANS_ID", cardMapper.asTo(card), 80.0, 100.0, 80.0, transaction.registered, false)
        val actual = userTransactionMapper.asPaySalaryTo(transaction)
        assertThat(expected).usingRecursiveComparison().ignoringFields("registered", "card").isEqualTo(actual)
        assertMatch(expected.card, actual.card)
    }
}