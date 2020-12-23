package munoon.bank.service.transactional.transaction.cancel

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.BuyCardTo
import munoon.bank.service.transactional.card.CardDataTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

internal class CancelBuyCardTransactionOperatorTest : AbstractTest() {
    @Autowired
    private lateinit var cancelBuyCardTransactionOperator: CancelBuyCardTransactionOperator

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Test
    fun cancel() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        cardService.plusMoney(card, 200.0)
        val boughtCard = cardService.buyCard(100, BuyCardTo("gold", "1111", CardDataTo(card.number!!, "1111")))
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(85.0)
        val transaction = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content[0]
        assertThat(transaction.type).isEqualTo(UserTransactionType.CARD_BUY)
        val result = cancelBuyCardTransactionOperator.cancel(transaction, emptySet())
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
        cancelBuyCardTransactionOperator.cancel(transaction, setOf(CancelTransactionFlag.DEACTIVATE_CARD))
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(200.0)
        assertThat(cardService.getCardById(boughtCard.id!!).active).isFalse()
    }

    @Test
    fun checkTrue() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val transaction = UserTransaction("abc", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.CARD_BUY, BuyCardUserTransactionInfo(card), false)
        val result = cancelBuyCardTransactionOperator.check(transaction)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val transaction = UserTransaction("abc", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(100, "test"), false)
        val result = cancelBuyCardTransactionOperator.check(transaction)
        assertThat(result).isFalse()
    }
}