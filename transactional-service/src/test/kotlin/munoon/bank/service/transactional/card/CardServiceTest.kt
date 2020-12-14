package munoon.bank.service.transactional.card

import munoon.bank.common.util.exception.FieldValidationException
import munoon.bank.common.util.exception.NotFoundException
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
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import javax.validation.ValidationException

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
        val expected = Card(card.id, 100, "default", null, "", 0.0, true, LocalDateTime.now())
        assertMatch(cardService.getCardsByUserId(100), expected)

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
        val expectedTransaction = UserTransaction(page.content[0].id, defaultCard.copy(balance = 885.0), 115.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, BuyCardUserTransactionInfo(goldCard, 100.0))
        assertMatch(page.content, expectedTransaction)
    }

    @Test
    fun buyPaymentCardNotOwnCard() {
        val defaultCardNumber = "123456789012"
        val defaultCardPinCode = "1111"
        cardService.buyCard(100, BuyCardTo("default", defaultCardPinCode, null)).let {
            cardRepository.save(it.copy(balance =  1000.0, number = defaultCardNumber))
        }
        assertThrows<AccessDeniedException> {
            cardService.buyCard(101, BuyCardTo("gold", "1111", CardDataTo(defaultCardNumber, defaultCardPinCode)))
        }
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
    fun buyCardPaymentCardNotActive() {
        val defaultCardNumber = "123456789012"
        val defaultCardPinCode = "1111"
        cardService.buyCard(100, BuyCardTo("default", defaultCardPinCode, null)).let {
            cardRepository.save(it.copy(balance =  1000.0, number = defaultCardNumber, active = false))
        }

        assertThrows<AccessDeniedException> {
            cardService.buyCard(100, BuyCardTo("gold", "1111", CardDataTo(defaultCardNumber, defaultCardPinCode)))
        }
    }

    @Test
    fun getCardsByUserId() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null))
        val expected = Card(card.id, 100, "default", null, "", 0.0, true, LocalDateTime.now())
        assertMatch(cardService.getCardsByUserId(100), expected)
    }

    @Test
    fun getCardByNumberAndValidatePinCode() {
        val cardNumber = "123456789012"
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(number = cardNumber))
        }

        val expected = Card(card.id, 100, "default", cardNumber, "", 0.0, true, LocalDateTime.now())
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

        val expected = Card(card.id, 100, "default", cardNumber, "", 0.0, true, LocalDateTime.now())
        assertMatch(cardService.getCardByNumber(cardNumber), expected)
    }

    @Test
    fun minusMoney() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 100.0))
        }
        cardService.minusMoney(card, 100.0)

        val expected = Card(card.id, 100, "default", null, "", 0.0, true, LocalDateTime.now())
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
    fun minusMoneyNotChecked() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 100.0))
        }
        assertDoesNotThrow { cardService.minusMoney(card, 999.0, checkBalance = false) }
    }

    @Test
    fun plusMoney() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        cardService.plusMoney(card, 100.0)
        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 100.0))
    }

    @Test
    fun getCardById() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null))
        val expected = Card(card.id, 100, "default", null, "", 0.0, true, LocalDateTime.now())
        assertMatch(cardService.getCardById(card.id!!), expected)
    }

    @Test
    fun createCard() {
        val createCard = AdminCreateCardTo(100, "default", "111111111111", "1111", true)
        val card = cardService.createCard(createCard)
        val expected = Card(card.id, 100, "default", "111111111111", "", 0.0, true, LocalDateTime.now())
        assertMatch(cardService.getCardsByUserId(100), expected)
        assertThat(passwordEncoder.matches("1111", cardService.getCardById(card.id!!).pinCode)).isTrue()
    }

    @Test
    fun createCardNotUniqueNumber() {
        val cardNumber = "111111111111"
        cardService.createCard(AdminCreateCardTo(100, "default", cardNumber, "1111", true))
        assertThrows<DuplicateKeyException> {
            cardService.createCard(AdminCreateCardTo(100, "default", cardNumber, "1111", true))
        }
    }

    @Test
    fun updateCard() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        cardService.updateCard(100, card.id!!, AdminUpdateCardTo(101, "gold", "111111111111", true))
        val expected = Card(card.id, 101, "gold", "111111111111", "", 0.0, true, card.registered)
        assertMatch(cardService.getCardsByUserId(101), expected)
    }

    @Test
    fun updateCardNotFound() {
        assertThrows<NotFoundException> {
            cardService.updateCard(100, "123", AdminUpdateCardTo(101, "gold", "111111111111", true))
        }
    }

    @Test
    fun updateCardBelongToOtherUser() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        assertThrows<AccessDeniedException> {
            cardService.updateCard(101, card.id!!, AdminUpdateCardTo(101, "gold", "111111111111", true))
        }
    }

    @Test
    fun updateCardNotUniqueNumber() {
        val number = "111111111111"
        cardService.createCard(AdminCreateCardTo(100, "default", number, "1111", true))
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        assertThrows<DuplicateKeyException> {
            cardService.updateCard(100, card.id!!, AdminUpdateCardTo(101, "gold", number, true))
        }
    }

    @Test
    fun updateCardPinCodeAdmin() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        cardService.updateCardPinCode(100, card.id!!, "2222")
        assertThat(passwordEncoder.matches("2222", cardService.getCardById(card.id!!).pinCode)).isTrue()
    }

    @Test
    fun updateCardPinCodeCardNotFoundAdmin() {
        assertThrows<NotFoundException> {
            cardService.updateCardPinCode(100, "123456", "2222")
        }
    }

    @Test
    fun updateCardPinCodeCardBelongToOtherUser() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        assertThrows<AccessDeniedException> {
            cardService.updateCardPinCode(101, card.id!!, "2222")
        }
    }

    @Test
    fun cardWithOutNumberNotUnique() {
        assertDoesNotThrow {
            cardRepository.save(Card(null, 100, "default", null, "", 0.0, true, LocalDateTime.now()))
            cardRepository.save(Card(null, 100, "default", null, "", 0.0, true, LocalDateTime.now()))
        }
    }

    @Test
    fun updateCardPinCode() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        cardService.updateCardPinCode(100, card.id!!, UserUpdateCardPinCode("1111", "2222"))

        val actual = cardService.getCardById(card.id!!)
        assertThat(passwordEncoder.matches("2222", actual.pinCode)).isTrue()
    }

    @Test
    fun updateCardPinCodeOldPinCodeIncorrect() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        assertThrows<FieldValidationException> {
            cardService.updateCardPinCode(100, card.id!!, UserUpdateCardPinCode("2222", "2222"))
        }
    }

    @Test
    fun updateCardPinCodeCardNotFound() {
        assertThrows<NotFoundException> {
            cardService.updateCardPinCode(100, "abc", UserUpdateCardPinCode("2222", "2222"))
        }
    }

    @Test
    fun updateCardPinCodeNotOwnCard() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        assertThrows<AccessDeniedException> {
            cardService.updateCardPinCode(101, card.id!!, UserUpdateCardPinCode("1111", "2222"))
        }
    }
}