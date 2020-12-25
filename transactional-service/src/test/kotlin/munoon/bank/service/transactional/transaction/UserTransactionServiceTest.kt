package munoon.bank.service.transactional.transaction

import munoon.bank.common.util.exception.ApplicationException
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import munoon.bank.service.transactional.util.NotEnoughBalanceException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, null, false)
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

        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, BuyCardUserTransactionInfo(createdCard), false)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun getTransactions() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val transaction = userTransactionService.buyCardTransaction(100, 100.0, CardDataTo(card.number!!, "1111"))
        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, null, false)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun fineTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true)).let {
            cardService.plusMoney(it, 1000.0)
        }
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.FINE, "message"))

        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 885.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "message"), false)
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun awardTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))

        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 85.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 85.0), 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
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
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.FINE, null))

        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = -115.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = -115.0), 115.0, 100.0, -115.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, null), false)
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

    @Test
    fun translateMoney() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        val receiverCard = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        cardService.plusMoney(senderCard, 200.0)

        val transaction = userTransactionService.translateMoney(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))

        assertThat(cardService.getCardById(senderCard.id!!).balance).isEqualTo(85.0)
        assertThat(cardService.getCardById(receiverCard.id!!).balance).isEqualTo(100.0)

        val receiverTransactionId = (transaction.info as TranslateUserTransactionInfo).receiveTransactionId
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
        assertMatch(userTransactionService.getTransactions(senderCard.id!!, 100, request).content, expectedSenderTransaction)
        assertMatch(userTransactionService.getTransactions(receiverCard.id!!, 100, request).content, expectedReceiverTransaction)
    }

    @Test
    fun translateCardNotOwn() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        cardService.plusMoney(senderCard, 200.0)

        assertThrows<AccessDeniedException> {
            userTransactionService.translateMoney(101, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        }
    }

    @Test
    fun translateCardNotActive() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", false))
        cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        cardService.plusMoney(senderCard, 200.0)

        assertThrows<AccessDeniedException> {
            userTransactionService.translateMoney(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        }
    }

    @Test
    fun translateCardReceiverNotActive() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        val receiverCard = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", false))
        cardService.plusMoney(senderCard, 200.0)

        assertThrows<AccessDeniedException> {
            userTransactionService.translateMoney(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        }
    }

    @Test
    fun translateCardNotEnoughMoney() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        val receiverCard = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))

        assertThrows<NotEnoughBalanceException> {
            userTransactionService.translateMoney(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        }
    }

    @Test
    fun cancelTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))

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
        val transaction = userTransactionService.fineAwardTransaction(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))
        userTransactionService.cancelTransaction(transaction.id!!, emptySet())
        assertThrows<ApplicationException> { userTransactionService.cancelTransaction(transaction.id!!, emptySet()) }
    }
}