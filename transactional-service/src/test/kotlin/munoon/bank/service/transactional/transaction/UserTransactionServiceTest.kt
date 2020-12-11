package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime

internal class UserTransactionServiceTest : AbstractTest() {
    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Test
    fun buyCardTransaction() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val transaction = userTransactionService.buyCardTransaction(100, 100.0, CardDataTo(card.number!!, "1111"))
        val expected = UserTransaction(transaction.id, card.copy(balance = 900.0), 100.0, 900.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, null)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun buyCardTransactionNotOwnCard() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        assertThrows<AccessDeniedException> {
            userTransactionService.buyCardTransaction(101, 100.0, CardDataTo(card.number!!, "1111"))
        }
    }

    @Test
    fun buyCardTransactionNotActive() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012", active = false))
        }

        assertThrows<AccessDeniedException> {
            userTransactionService.buyCardTransaction(100, 100.0, CardDataTo(card.number!!, "1111"))
        }
    }

    @Test
    fun addCardToCardTransaction() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val transaction = userTransactionService.buyCardTransaction(100, 100.0, CardDataTo(card.number!!, "1111"))

        val createdCard = cardRepository.save(Card(null, 100, "default", "121212121212", "{noop}1111", 0.0, true, LocalDateTime.now()))
        userTransactionService.addCardToCardTransaction(transaction, createdCard)

        val expected = UserTransaction(transaction.id, card.copy(balance = 900.0), 100.0, 900.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, BuyCardUserTransactionInfo(createdCard))
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun getTransactions() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val transaction = userTransactionService.buyCardTransaction(100, 100.0, CardDataTo(card.number!!, "1111"))
        val expected = UserTransaction(transaction.id, card.copy(balance = 900.0), 100.0, 900.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, null)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }
}