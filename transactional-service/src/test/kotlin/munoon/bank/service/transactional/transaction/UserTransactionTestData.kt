package munoon.bank.service.transactional.transaction

import com.fasterxml.jackson.module.kotlin.readValue
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.JsonUtil.getContent
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object UserTransactionTestData {
    fun assertMatchTo(actual: Iterable<UserTransactionTo>, expected: Iterable<UserTransactionTo>) {
        assertThat(actual).usingElementComparatorIgnoringFields("card", "info", "registered").isEqualTo(expected)
        assertThat(actual.map { it.card }).usingElementComparatorIgnoringFields("registered").isEqualTo(expected.map { it.card })
        assertThat(actual.map { it.info }).usingElementComparatorIgnoringFields("buyCard.registered").isEqualTo(expected.map { it.info })
    }

    fun assertMatch(actual: UserTransaction, expected: UserTransaction) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("card.registered", "card.pinCode", "info.buyCard.pinCode", "info.buyCard.registered", "registered")
                .isEqualTo(expected);
    }

    fun assertMatch(actual: List<UserTransaction>, expected: List<UserTransaction>) {
        assertThat(actual).hasSize(expected.size)
        actual.forEachIndexed { index, it -> assertMatch(it, expected[index]) }
    }

    fun assertMatch(actual: List<UserTransaction>, vararg expected: UserTransaction) {
        assertMatch(actual, expected.toList())
    }

    fun contentJsonList(vararg expected: UserTransactionTo) = ResultMatcher {
        val node = JsonUtil.OBJECT_MAPPER.readTree(getContent(it)).at("/content")
        val actual = JsonUtil.OBJECT_MAPPER.readValue<List<UserTransactionTo>>(node.toString())
        assertMatchTo(actual, expected.toList())
    }
}