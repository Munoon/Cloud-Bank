package munoon.bank.service.transactional.transaction.cancel

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardDataTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

internal class CancelTranslateTransactionOperationTest : AbstractTest() {
    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cancelTranslateTransactionOperator: CancelTranslateTransactionOperation

    @Test
    fun cancelTranslate() {
        val sender = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val receiver = cardService.createCard(AdminCreateCardTo(100, "default", "222222222222", "1111", true))
        cardService.plusMoney(sender, 200.0)
        val transaction = userTransactionService.translateMoney(100, TranslateMoneyDataTo(receiver.number!!, 100.0, "test", CardDataTo(sender.number!!, "1111")))
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(85.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(100.0)
        cancelTranslateTransactionOperator.cancel(transaction, emptySet())
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(200.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(0.0)
    }

    @Test
    fun cancelReceive() {
        val sender = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val receiver = cardService.createCard(AdminCreateCardTo(100, "default", "222222222222", "1111", true))
        cardService.plusMoney(sender, 200.0)
        val transaction = userTransactionService.translateMoney(100, TranslateMoneyDataTo(receiver.number!!, 100.0, "test", CardDataTo(sender.number!!, "1111")))
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(85.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(100.0)
        val receiverTransaction = userTransactionService.getTransaction((transaction.info as TranslateUserTransactionInfo).receiveTransactionId)
        cancelTranslateTransactionOperator.cancel(receiverTransaction, emptySet())
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(200.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(0.0)
    }

    @Test
    fun checkTrueTranslate() {
        val card = Card("CARD_ID", 100, "default", null, "1111", 10.0, true, LocalDateTime.now())
        val transaction = UserTransaction("TEST", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.TRANSLATE_MONEY, TranslateUserTransactionInfo("abc", 100, null), false)
        val result = cancelTranslateTransactionOperator.check(transaction)
        assertThat(result).isTrue()
    }

    @Test
    fun checkTrueReceive() {
        val card = Card("CARD_ID", 100, "default", null, "1111", 10.0, true, LocalDateTime.now())
        val transaction = UserTransaction("TEST", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.RECEIVE_MONEY, ReceiveUserTransactionInfo("abc", 100, null), false)
        val result = cancelTranslateTransactionOperator.check(transaction)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val card = Card("CARD_ID", 100, "default", null, "1111", 10.0, true, LocalDateTime.now())
        val transaction = UserTransaction("TEST", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.AWARD, null, false)
        val result = cancelTranslateTransactionOperator.check(transaction)
        assertThat(result).isFalse()
    }
}