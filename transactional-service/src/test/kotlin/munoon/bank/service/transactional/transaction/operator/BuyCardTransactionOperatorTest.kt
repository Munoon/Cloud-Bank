package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime

internal class BuyCardTransactionOperatorTest : AbstractTest() {
    @Autowired
    private lateinit var buyCardTransactionOperator: BuyCardTransactionOperator

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Autowired
    private lateinit var userTransactionService: UserTransactionService
    
    @Test
    fun buyCardTransaction() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val data = BuyCardTransactionInfoData(100, 100.0, CardDataTo(card.number!!, "1111"))
        val transaction = buyCardTransactionOperator.createTransaction(data)
        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, null, false)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun buyCardTransactionNotOwnCard() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val data = BuyCardTransactionInfoData(101, 100.0, CardDataTo(card.number!!, "1111"))
        assertThrows<AccessDeniedException> { buyCardTransactionOperator.createTransaction(data) }
    }

    @Test
    fun buyCardTransactionNotActive() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012", active = false))
        }

        val data = BuyCardTransactionInfoData(100, 100.0, CardDataTo(card.number!!, "1111"))
        assertThrows<AccessDeniedException> {
            buyCardTransactionOperator.createTransaction(data)
        }
    }

    @Test
    fun addCardToCardTransaction() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val data = BuyCardTransactionInfoData(100, 100.0, CardDataTo(card.number!!, "1111"))
        val transaction = buyCardTransactionOperator.createTransaction(data)

        val createdCard = cardRepository.save(Card(null, 100, "default", "121212121212", "{noop}1111", 0.0, true, LocalDateTime.now()))
        buyCardTransactionOperator.createTransactionNextStep(transaction, AddCardTransactionInfoData(createdCard), 1)

        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, BuyCardUserTransactionInfo(createdCard), false)
        assertMatch(userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content, expected)
    }

    @Test
    fun createTransactionNextStepNotSupported() {
        val card = cardService.buyCard(100, BuyCardTo("default", "1111", null)).let {
            cardRepository.save(it.copy(balance = 1000.0, number = "123456789012"))
        }

        val data = BuyCardTransactionInfoData(100, 100.0, CardDataTo(card.number!!, "1111"))
        val transaction = buyCardTransactionOperator.createTransaction(data)

        val createdCard = cardRepository.save(Card(null, 100, "default", "121212121212", "{noop}1111", 0.0, true, LocalDateTime.now()))

        assertThrows<IllegalArgumentException> {
            buyCardTransactionOperator.createTransactionNextStep(transaction, AddCardTransactionInfoData(createdCard), 2)
        }
    }

    @Test
    fun cancel() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        cardService.plusMoney(card, 200.0)
        val boughtCard = cardService.buyCard(100, BuyCardTo("gold", "1111", CardDataTo(card.number!!, "1111")))
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(85.0)
        val transaction = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content[0]
        assertThat(transaction.type).isEqualTo(UserTransactionType.CARD_BUY)
        val result = buyCardTransactionOperator.cancel(transaction, emptySet())
        assertThat(result).isTrue()
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(200.0)
        assertThat(cardService.getCardById(boughtCard.id!!).active).isTrue()
    }

    @Test
    fun cancelAndRemoveCard() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        cardService.plusMoney(card, 200.0)
        val boughtCard = cardService.buyCard(100, BuyCardTo("gold", "1111", CardDataTo(card.number!!, "1111")))
        val transaction = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content[0]
        buyCardTransactionOperator.cancel(transaction, setOf(CancelTransactionFlag.DEACTIVATE_CARD))
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(200.0)
        assertThat(cardService.getCardById(boughtCard.id!!).active).isFalse()
    }

    @Test
    fun checkTrue() {
        val result = buyCardTransactionOperator.check(UserTransactionType.CARD_BUY)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val result = buyCardTransactionOperator.check(UserTransactionType.AWARD)
        assertThat(result).isFalse()
    }
}