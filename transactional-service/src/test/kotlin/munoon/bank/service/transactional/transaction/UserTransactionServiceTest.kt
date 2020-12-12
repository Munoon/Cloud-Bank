package munoon.bank.service.transactional.transaction

import munoon.bank.common.util.exception.NotFoundException
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

    @Test
    fun fineTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true)).let {
            cardService.plusMoney(it, 100.0)
        }
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 10.0, FineAwardType.FINE, "message"))

        CardTestData.assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 90.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 90.0), 10.0, 90.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "message"))
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun awardTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 10.0, FineAwardType.AWARD, null))

        CardTestData.assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 10.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 10.0), 10.0, 10.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null))
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun fineAwardTransactionCardNotFound() {
        assertThrows<NotFoundException> {
            userTransactionService.fineAwardTransaction(101, FineAwardDataTo("abc", 10.0, FineAwardType.AWARD, null))
        }
    }

    @Test
    fun fineAwardTransactionIgnoreBalance() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 10.0, FineAwardType.FINE, null))

        CardTestData.assertMatch(cardService.getCardsByUserId(100), card.copy(balance = -10.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = -10.0), 10.0, -10.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, null))
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun fineAwardTransactionCardNotActive() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", false))
        assertThrows<AccessDeniedException> {
            userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 10.0, FineAwardType.AWARD, null))
        }
    }
}