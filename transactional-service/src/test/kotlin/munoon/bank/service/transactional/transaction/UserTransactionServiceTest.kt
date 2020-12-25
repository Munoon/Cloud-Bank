package munoon.bank.service.transactional.transaction

import munoon.bank.common.util.exception.ApplicationException
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

internal class UserTransactionServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Test
    fun makeTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))
        val transaction = userTransactionService.makeTransaction(data)

        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 85.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 85.0), 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun makeTransactionNextStep() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val data = BuyCardTransactionInfoData(100, 100.0, CardDataTo(card.number!!, "1111"))
        val transaction = userTransactionService.makeTransaction(data)

        val createdCard = cardRepository.save(Card(null, 100, "default", "121212121212", "{noop}1111", 0.0, true, LocalDateTime.now()))
        userTransactionService.makeTransactionNextStep(transaction, AddCardTransactionInfoData(createdCard), 1)

        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, BuyCardUserTransactionInfo(createdCard), false)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun getTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))
        val transaction = userTransactionService.makeTransaction(data)
        val expected = UserTransaction(transaction.id, card.copy(balance = 85.0), 85.0, 100.0, 85.0, transaction.registered, UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        assertMatch(userTransactionService.getTransaction(transaction.id!!), expected)
    }

    @Test
    fun getTransactionNotFound() {
        assertThrows<NotFoundException> {
            userTransactionService.getTransaction("abc")
        }
    }

    @Test
    fun getAll() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data1 = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))
        val data2 = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 50.0, FineAwardType.FINE, null))
        val transaction1 = userTransactionService.makeTransaction(data1)
        val transaction2 = userTransactionService.makeTransaction(data2)
        val expected1 = UserTransaction(transaction1.id, card.copy(balance = 27.5), 85.0, 100.0, 85.0, transaction1.registered, UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        val expected2 = UserTransaction(transaction2.id, card.copy(balance = 27.5), 57.5, 50.0, 27.5, transaction1.registered, UserTransactionType.FINE, FineUserTransactionInfo(101, null), false)
        val actual = userTransactionService.getAll(setOf(transaction1.id!!, transaction2.id!!))
        assertThat(actual).containsOnlyKeys(transaction1.id!!, transaction2.id!!)
        assertMatch(actual[transaction1.id]!!, expected1)
        assertMatch(actual[transaction2.id]!!, expected2)
    }

    @Test
    fun create() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val create = UserTransaction(null, card, 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        val actual = userTransactionService.create(create)
        val expected = UserTransaction(actual.id, card, 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        assertMatch(userTransactionService.getTransactions(card.id!!, null, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun createNotNew() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val create = UserTransaction("abc", card, 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        assertThrows<IllegalArgumentException> {
            userTransactionService.create(create)
        }
    }

    @Test
    fun update() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val create = UserTransaction(null, card, 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
                .let { userTransactionService.create(it) }
        userTransactionService.update(create.copy(price = 100.0, leftBalance = 100.0))
        val expected = UserTransaction(create.id, card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        assertMatch(userTransactionService.getTransactions(card.id!!, null, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun updateNotCreated() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val create = UserTransaction(null, card, 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        assertThrows<IllegalArgumentException> {
            userTransactionService.update(create)
        }
    }

    @Test
    fun getTransactions() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val data = BuyCardTransactionInfoData(100, 100.0, CardDataTo(card.number!!, "1111"))
        val transaction = userTransactionService.makeTransaction(data)
        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, null, false)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun cancelTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))
        val transaction = userTransactionService.makeTransaction(data)

        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(85.0)

        userTransactionService.cancelTransaction(transaction.id!!, emptySet())

        val expectedCard = Card(card.id, 100, "default", "111111111111", "", 0.0, true, card.registered)
        assertMatch(cardService.getCardById(card.id!!), expectedCard)

        val expected = UserTransaction(transaction.id, cardService.getCardById(card.id!!), 85.0, 100.0, 85.0, transaction.registered, UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), true)
        assertMatch(userTransactionService.getTransactions(card.id!!, null, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun cancelTransactionNotFound() {
        assertThrows<NotFoundException> {
            userTransactionService.cancelTransaction("abc", emptySet())
        }
    }

    @Test
    fun cancelTransactionAlreadyCanceled() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))
        val transaction = userTransactionService.makeTransaction(data)
        userTransactionService.cancelTransaction(transaction.id!!, emptySet())
        assertThrows<ApplicationException> { userTransactionService.cancelTransaction(transaction.id!!, emptySet()) }
    }
}