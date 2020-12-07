package munoon.bank.service.transactional.card

import com.fasterxml.jackson.module.kotlin.readValue
import munoon.bank.service.transactional.util.JsonUtil
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object CardTestData {
    fun assertMatch(actual: Card, expected: Card) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("pinCode").isEqualTo(expected)
    }

    fun assertMatch(actual: Iterable<Card>, expected: Iterable<Card>) {
        assertThat(actual).usingElementComparatorIgnoringFields("pinCode").isEqualTo(expected)
    }

    fun assertMatch(actual: Iterable<Card>, vararg expected: Card) {
        assertMatch(actual, expected.toList())
    }

    fun assertMatch(actual: CardTo, expected: CardTo) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    fun assertMatchTo(actual: Iterable<CardTo>, expected: Iterable<CardTo>) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    fun contentJson(vararg expected: CardTo) = ResultMatcher {
        val actual = JsonUtil.OBJECT_MAPPER.readValue<List<CardTo>>(JsonUtil.getContent(it))
        assertMatchTo(actual, expected.toList())
    }
}