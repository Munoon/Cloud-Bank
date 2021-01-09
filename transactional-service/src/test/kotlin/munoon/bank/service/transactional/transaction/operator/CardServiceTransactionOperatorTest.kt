package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.CardServiceTransactionInfoData
import munoon.bank.service.transactional.transaction.UserTransaction
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

internal class CardServiceTransactionOperatorTest : AbstractTest() {
    @Autowired
    private lateinit var cardServiceTransactionOperator: CardServiceTransactionOperator

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var transactionService: UserTransactionService

    @Test
    fun createTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "gold", null, "1111", true))
        val data = CardServiceTransactionInfoData(card.id!!)
        val transaction = cardServiceTransactionOperator.createTransaction(data)!!
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(-10.0)

        val expected = UserTransaction(transaction.id, card.copy(balance = -10.0), 10.0, 10.0, -10.0, transaction.registered, UserTransactionType.CARD_SERVICE, null, false)
        val actual = transactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10)).content
        assertMatch(actual, expected)
    }

    @Test
    fun cancel() {
        val card = cardService.createCard(AdminCreateCardTo(100, "gold", null, "1111", true))
        val data = CardServiceTransactionInfoData(card.id!!)
        val transaction = cardServiceTransactionOperator.createTransaction(data)!!
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(-10.0)

        val result = cardServiceTransactionOperator.cancel(transaction)
        assertThat(result).isTrue()
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(0.0)
    }

    @Test
    fun checkTrue() {
        val result = cardServiceTransactionOperator.check(UserTransactionType.CARD_SERVICE)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val result = cardServiceTransactionOperator.check(UserTransactionType.AWARD)
        assertThat(result).isFalse()
    }
}