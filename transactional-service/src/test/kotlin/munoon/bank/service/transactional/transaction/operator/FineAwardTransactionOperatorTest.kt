package munoon.bank.service.transactional.transaction.operator

import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime

internal class FineAwardTransactionOperatorTest : AbstractTest() {
    @Autowired
    private lateinit var fineAwardTransactionOperator: FineAwardTransactionOperator

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Test
    fun fineTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true)).let {
            cardService.plusMoney(it, 1000.0)
        }
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.FINE, "message"))
        val transaction = fineAwardTransactionOperator.createTransaction(data)

        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 885.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 885.0), 115.0, 100.0, 885.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "message"), false)
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun awardTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.AWARD, null))
        val transaction = fineAwardTransactionOperator.createTransaction(data)

        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = 85.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = 85.0), 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(101, null), false)
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun fineAwardTransactionCardNotFound() {
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo("abc", 10.0, FineAwardType.AWARD, null))
        assertThrows<NotFoundException> { fineAwardTransactionOperator.createTransaction(data) }
    }

    @Test
    fun fineAwardTransactionIgnoreBalance() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 100.0, FineAwardType.FINE, null))
        val transaction = fineAwardTransactionOperator.createTransaction(data)

        assertMatch(cardService.getCardsByUserId(100), card.copy(balance = -115.0))

        val expected = UserTransaction(transaction.id, card.copy(balance = -115.0), 115.0, 100.0, -115.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, null), false)
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun fineAwardTransactionCardNotActive() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", false))
        val data = FineAwardTransactionInfoData(101, FineAwardDataTo(card.number!!, 10.0, FineAwardType.AWARD, null))
        assertThrows<AccessDeniedException> { fineAwardTransactionOperator.createTransaction(data) }
    }

    @Test
    fun awardCancel() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val transaction = UserTransaction("abc", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.AWARD, AwardUserTransactionInfo(100, "test"), false)
        val result = fineAwardTransactionOperator.cancel(transaction, emptySet())
        assertThat(result).isTrue()
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(-100.0)
    }

    @Test
    fun fineCancel() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", null, "1111", true))
        val transaction = UserTransaction("abc", card, 100.0, 100.0, 100.0, LocalDateTime.now(), UserTransactionType.FINE, AwardUserTransactionInfo(100, "test"), false)
        val result = fineAwardTransactionOperator.cancel(transaction, emptySet())
        assertThat(result).isTrue()
        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(100.0)
    }

    @Test
    fun checkAward() {
        val result = fineAwardTransactionOperator.check(UserTransactionType.AWARD)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFine() {
        val result = fineAwardTransactionOperator.check(UserTransactionType.FINE)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val result = fineAwardTransactionOperator.check(UserTransactionType.TRANSLATE_MONEY)
        assertThat(result).isFalse()
    }
}