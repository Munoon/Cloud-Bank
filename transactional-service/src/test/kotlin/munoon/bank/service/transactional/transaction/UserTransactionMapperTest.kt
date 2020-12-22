package munoon.bank.service.transactional.transaction

import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardMapper
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatchTo
import munoon.bank.service.transactional.user.UserService
import munoon.bank.service.transactional.user.UserTestData
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
    fun asTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asTo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
    }

    @Test
    fun asToWithCustomUsers() {
        val users = mapOf(
                100 to UserTestData.DEFAULT_USER_TO,
                101 to user101
        )

        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asTo(transaction, users), expected)

        verify(userService, times(0)).getUsersById(anySet())
    }

    @Test
    fun asToPage() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)

        val transaction2 = UserTransaction("321", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, "test2"), true)
        val expected2 = UserTransactionTo("321", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfoTo(user101, "test2"), true)

        val expectedPage = PageImpl(listOf(expected, expected2))
        val actualPage = userTransactionMapper.asTo(PageImpl(listOf(transaction, transaction2)))
        assertMatchTo(actualPage.content, expectedPage.content)

        verify(userService, times(1)).getUsersById(anySet())
    }

    @Test
    fun buyCardAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, BuyCardUserTransactionInfoTo(cardMapper.asTo(card)), true)
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, BuyCardUserTransactionInfo(card), true)
        assertMatch(userTransactionMapper.asTo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
    }

    @Test
    fun awardAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, AwardUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, AwardUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asTo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
    }

    @Test
    fun fineAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"), true)
        val expected = UserTransactionTo("123", cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO), 10.0, 100.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(user101, "test"), true)
        assertMatch(userTransactionMapper.asTo(transaction), expected)
        verify(userService, times(1)).getUsersById(anySet())
    }
}