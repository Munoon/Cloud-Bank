package munoon.bank.service.transactional.card

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.transaction.BuyCardUserTransactionInfo
import munoon.bank.service.transactional.transaction.UserTransaction
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionType
import munoon.bank.service.transactional.util.NotEnoughBalanceException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

internal class CardServiceTest : AbstractTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var transactionService: UserTransactionService

    @Test
    fun buyFreeCard() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null))
        val expected = Card(card.id, 100, "default", null, "", 0.0, LocalDateTime.now())
        assertMatch(cardService.getCards(100), expected)

        val actualCard = cardService.getCardById(card.id!!)
        assertThat(passwordEncoder.matches("1111", actualCard.pinCode)).isTrue()
    }

    @Test
    fun buyPaymentCard() {
        val defaultCardNumber = "123456789012"
        val defaultCardPinCode = "1111"
        val defaultCard = cardService.buyCard(100, BuyCardTo("default", defaultCardPinCode, null)).let {
            cardRepository.save(it.copy(balance =  1000.0, number = defaultCardNumber))
        }

        val goldCard = cardService.buyCard(100, BuyCardTo("gold", "1111", CardDataTo(defaultCardNumber, defaultCardPinCode)))
        val page = transactionService.getTransactions(defaultCard.id!!, 100, PageRequest.of(0, 10))
        assertThat(page.content.size).isEqualTo(1)
        val expectedTransaction = UserTransaction(page.content[0].id, defaultCard.copy(balance = 900.0), 100.0, 900.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, BuyCardUserTransactionInfo(goldCard))
        assertMatch(page.content, expectedTransaction)
    }

    @Test
    fun buyCardCantBuy() {
        assertThrows<AccessDeniedException> { cardService.buyCard(100, BuyCardTo("parliament", "1111", null)) }
    }

    @Test
    fun buyCardLimitExceed() {
        cardService.buyCard(100, BuyCardTo("default", "1111", null))
        assertThrows<AccessDeniedException> { cardService.buyCard(100, BuyCardTo("default", "1111", null)) }
    }

    @Test
    fun buyCardNoPaymentCard() {
        assertThrows<AccessDeniedException> { cardService.buyCard(100, BuyCardTo("gold", "1111", null)) }
    }

    @Test
    fun buyCardNotEnoughBalance() {
        val defaultCardNumber = "123456789012"
        val defaultCardPinCode = "1111"
        cardService.buyCard(100, BuyCardTo("default", defaultCardPinCode, null)).apply {
            cardRepository.save(copy(number = defaultCardNumber))
        }

        assertThrows<NotEnoughBalanceException> {
            cardService.buyCard(100, BuyCardTo("gold", "1111", CardDataTo(defaultCardNumber, defaultCardPinCode)))
        }
    }

    @Test
    fun getCards() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null))
        val expected = Card(card.id, 100, "default", null, "", 0.0, LocalDateTime.now())
        assertMatch(cardService.getCards(100), expected)
    }

    @Test
    fun getCardByNumberAndValidatePinCode() {
        val cardNumber = "123456789012"
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(number = cardNumber))
        }

        val expected = Card(card.id, 100, "default", cardNumber, "", 0.0, LocalDateTime.now())
        assertMatch(cardService.getCardByNumberAndValidatePinCode(cardNumber, "1111"), expected)
    }

    @Test
    fun getCardByNumberAndValidatePinCodeInvalidPinCode() {
        val cardNumber = "123456789012"
        cardService.buyCard(100, BuyCardTo("default", "1111", null)).apply {
            cardRepository.save(copy(number = cardNumber))
        }

        assertThrows<AccessDeniedException> { cardService.getCardByNumberAndValidatePinCode(cardNumber, "2222") }
    }

    @Test
    fun getCardByNumber() {
        val cardNumber = "123456789012"
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(number = cardNumber))
        }

        val expected = Card(card.id, 100, "default", cardNumber, "", 0.0, LocalDateTime.now())
        assertMatch(cardService.getCardByNumber(cardNumber), expected)
    }

    @Test
    fun minusMoney() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 100.0))
        }
        cardService.minusMoney(card, 100.0)

        val expected = Card(card.id, 100, "default", null, "", 0.0, LocalDateTime.now())
        assertMatch(cardService.getCardById(card.id!!), expected)
    }

    @Test
    fun minusMoneyNotEnough() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 100.0))
        }
        assertThrows<NotEnoughBalanceException> { cardService.minusMoney(card, 999.0) }
    }

    @Test
    fun getCardById() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null))
        val expected = Card(card.id, 100, "default", null, "", 0.0, LocalDateTime.now())
        assertMatch(cardService.getCardById(card.id!!), expected)
    }
}