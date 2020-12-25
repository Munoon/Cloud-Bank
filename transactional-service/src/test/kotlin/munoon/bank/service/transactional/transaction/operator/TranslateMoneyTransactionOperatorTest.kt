package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardDataTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.util.NotEnoughBalanceException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException

internal class TranslateMoneyTransactionOperatorTest : AbstractTest() {
    @Autowired
    private lateinit var translateMoneyTransactionOperator: TranslateMoneyTransactionOperator

    @Autowired
    private lateinit var cardService: CardService
    
    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Test
    fun translateMoney() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        val receiverCard = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        cardService.plusMoney(senderCard, 200.0)

        val data = TranslateMoneyTransactionInfoData(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        val transaction = translateMoneyTransactionOperator.createTransaction(data)

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
        UserTransactionTestData.assertMatch(userTransactionService.getTransactions(senderCard.id!!, 100, request).content, expectedSenderTransaction)
        UserTransactionTestData.assertMatch(userTransactionService.getTransactions(receiverCard.id!!, 100, request).content, expectedReceiverTransaction)
    }

    @Test
    fun translateCardNotOwn() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        cardService.plusMoney(senderCard, 200.0)

        val data = TranslateMoneyTransactionInfoData(101, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        assertThrows<AccessDeniedException> { translateMoneyTransactionOperator.createTransaction(data) }
    }

    @Test
    fun translateCardNotActive() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", false))
        cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        cardService.plusMoney(senderCard, 200.0)

        val data = TranslateMoneyTransactionInfoData(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        assertThrows<AccessDeniedException> { translateMoneyTransactionOperator.createTransaction(data) }
    }

    @Test
    fun translateCardReceiverNotActive() {
        val senderCard = cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", false))
        cardService.plusMoney(senderCard, 200.0)

        val data = TranslateMoneyTransactionInfoData(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        assertThrows<AccessDeniedException> { translateMoneyTransactionOperator.createTransaction(data) }
    }

    @Test
    fun translateCardNotEnoughMoney() {
        cardService.createCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true))
        cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))

        val data = TranslateMoneyTransactionInfoData(100, TranslateMoneyDataTo("111111111111", 100.0, "test", CardDataTo("123456789012", "1111")))
        assertThrows<NotEnoughBalanceException> { translateMoneyTransactionOperator.createTransaction(data) }
    }

    @Test
    fun cancelTranslate() {
        val sender = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val receiver = cardService.createCard(AdminCreateCardTo(100, "default", "222222222222", "1111", true))
        cardService.plusMoney(sender, 200.0)
        val data = TranslateMoneyTransactionInfoData(100, TranslateMoneyDataTo(receiver.number!!, 100.0, "test", CardDataTo(sender.number!!, "1111")))
        val transaction = translateMoneyTransactionOperator.createTransaction(data)
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(85.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(100.0)
        translateMoneyTransactionOperator.cancel(transaction, emptySet())
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(200.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(0.0)
    }

    @Test
    fun cancelReceive() {
        val sender = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val receiver = cardService.createCard(AdminCreateCardTo(100, "default", "222222222222", "1111", true))
        cardService.plusMoney(sender, 200.0)
        val data = TranslateMoneyTransactionInfoData(100, TranslateMoneyDataTo(receiver.number!!, 100.0, "test", CardDataTo(sender.number!!, "1111")))
        val transaction = translateMoneyTransactionOperator.createTransaction(data)
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(85.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(100.0)
        val receiverTransaction = userTransactionService.getTransaction((transaction.info as TranslateUserTransactionInfo).receiveTransactionId)
        translateMoneyTransactionOperator.cancel(receiverTransaction, emptySet())
        assertThat(cardService.getCardById(sender.id!!).balance).isEqualTo(200.0)
        assertThat(cardService.getCardById(receiver.id!!).balance).isEqualTo(0.0)
    }

    @Test
    fun checkTrueTranslate() {
        val result = translateMoneyTransactionOperator.check(UserTransactionType.TRANSLATE_MONEY)
        assertThat(result).isTrue()
    }

    @Test
    fun checkTrueReceive() {
        val result = translateMoneyTransactionOperator.check(UserTransactionType.RECEIVE_MONEY)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val result = translateMoneyTransactionOperator.check(UserTransactionType.AWARD)
        assertThat(result).isFalse()
    }
}