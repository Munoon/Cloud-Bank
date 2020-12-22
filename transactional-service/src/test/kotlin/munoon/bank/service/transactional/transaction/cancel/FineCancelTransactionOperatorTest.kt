package munoon.bank.service.transactional.transaction.cancel

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.AwardUserTransactionInfo
import munoon.bank.service.transactional.transaction.FineCancelTransactionOperator
import munoon.bank.service.transactional.transaction.UserTransaction
import munoon.bank.service.transactional.transaction.UserTransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

internal class FineCancelTransactionOperatorTest : AbstractTest() {
    @Autowired
    private lateinit var fineCancelTransactionOperator: FineCancelTransactionOperator

    @Autowired
    private lateinit var cardService: CardService

    @Test
    fun cancel() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val transaction = UserTransaction("abc", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.FINE, AwardUserTransactionInfo(100, "test"), false)
        val result = fineCancelTransactionOperator.cancel(transaction)
        assertThat(result).isTrue()
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(100.0)
    }

    @Test
    fun checkTrue() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val transaction = UserTransaction("abc", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.FINE, AwardUserTransactionInfo(100, "test"), false)
        val result = fineCancelTransactionOperator.check(transaction)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val transaction = UserTransaction("abc", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(100, "test"), false)
        val result = fineCancelTransactionOperator.check(transaction)
        assertThat(result).isFalse()
    }
}