package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.asTo
import munoon.bank.service.transactional.transaction.UserTransactionTestData.assertMatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

internal class UserTransactionMapperTest {
    @Test
    fun asTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val transaction = UserTransaction("123", card, 10.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfo(101, "test"))
        val expected = UserTransactionTo("123", card.asTo(), 10.0, 110.0, LocalDateTime.now(), UserTransactionType.FINE, FineUserTransactionInfoTo(101, "test"))
        assertMatch(UserTransactionMapper.INSTANCE.asTo(transaction), expected)
    }

    @Test
    fun buyCardAsTo() {
        val card = Card("111", 100, "default", "111111111111", "1111", 100.0, true, LocalDateTime.now())
        val info = BuyCardUserTransactionInfo(card)
        val actual = UserTransactionMapper.INSTANCE.asTo(info)
        val expected = BuyCardUserTransactionInfoTo(card.asTo())
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun awardAsTo() {
        val info = AwardUserTransactionInfo(100, "test")
        val actual = UserTransactionMapper.INSTANCE.asTo(info)
        val expected = AwardUserTransactionInfoTo(100, "test")
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun fineAsTo() {
        val info = FineUserTransactionInfo(100, "test")
        val actual = UserTransactionMapper.INSTANCE.asTo(info)
        val expected = FineUserTransactionInfoTo(100, "test")
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}