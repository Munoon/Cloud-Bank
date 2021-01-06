package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardRepository
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.SalaryTransactionInfoData
import munoon.bank.service.transactional.transaction.UserTransaction
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import munoon.bank.service.transactional.transaction.UserTransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

internal class SalaryTransactionOperatorTest : AbstractTest() {
    @Autowired
    private lateinit var salaryTransactionOperator: SalaryTransactionOperator

    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Test
    fun createTransaction() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = SalaryTransactionInfoData(100, 100.0)
        val transaction = salaryTransactionOperator.createTransaction(data)!!

        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(85.0)

        val expected = UserTransaction(transaction.id, card.copy(balance = 85.0), 85.0, 100.0, 85.0, LocalDateTime.now(), UserTransactionType.SALARY, null, false)
        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertMatch(actual.content, expected)
    }

    @Test
    fun createTransactionNoPrimaryCard() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true)).let {
            cardRepository.save(it.copy(primary = false))
        }
        val data = SalaryTransactionInfoData(100, 100.0)
        val transaction = salaryTransactionOperator.createTransaction(data)
        assertThat(transaction).isNull()

        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(0.0)

        val actual = userTransactionService.getTransactions(card.id!!, 100, PageRequest.of(0, 10))
        assertThat(actual.content).hasSize(0)
    }

    @Test
    fun cancel() {
        val card = cardService.createCard(AdminCreateCardTo(100, "default", "111111111111", "1111", true))
        val data = SalaryTransactionInfoData(100, 100.0)
        val transaction = salaryTransactionOperator.createTransaction(data)!!

        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(85.0)

        val result = salaryTransactionOperator.cancel(transaction)
        assertThat(result).isTrue()

        assertThat(cardService.getCardById(card.id!!).balance).isEqualTo(0.0)
    }

    @Test
    fun checkTrue() {
        val result = salaryTransactionOperator.check(UserTransactionType.SALARY)
        assertThat(result).isTrue()
    }

    @Test
    fun checkFalse() {
        val result = salaryTransactionOperator.check(UserTransactionType.AWARD)
        assertThat(result).isFalse()
    }
}