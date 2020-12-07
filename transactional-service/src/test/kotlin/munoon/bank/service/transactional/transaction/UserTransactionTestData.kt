package munoon.bank.service.transactional.transaction

import com.fasterxml.jackson.module.kotlin.readValue
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.JsonUtil.getContent
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object UserTransactionTestData {
    fun assertMatchTo(actual: Iterable<UserTransactionTo>, expected: Iterable<UserTransactionTo>) {
        assertThat(actual).usingDefaultElementComparator().isEqualTo(expected)
    }

    fun assertMatch(actual: Iterable<UserTransaction>, expected: Iterable<UserTransaction>) {
        assertThat(actual).usingElementComparatorIgnoringFields("card.pinCode", "info.buyCard.pinCode").isEqualTo(expected)
    }

    fun assertMatch(actual: Iterable<UserTransaction>, vararg expected: UserTransaction) {
        assertMatch(actual, expected.toList())
    }

    fun contentJsonList(vararg expected: UserTransactionTo) = ResultMatcher {
        val node = JsonUtil.OBJECT_MAPPER.readTree(getContent(it)).at("/content")
        val actual = JsonUtil.OBJECT_MAPPER.readValue<List<UserTransactionTo>>(node.toString())
        assertMatchTo(actual, expected.toList())
    }
}