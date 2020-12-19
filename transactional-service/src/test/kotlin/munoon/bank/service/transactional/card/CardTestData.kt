package munoon.bank.service.transactional.card

import com.fasterxml.jackson.module.kotlin.readValue
import munoon.bank.service.transactional.user.UserTestData.assertMatch
import munoon.bank.service.transactional.util.JsonUtil
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object CardTestData {
    fun assertMatch(actual: Card, expected: Card) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("pinCode", "registered").isEqualTo(expected)
    }

    fun assertMatch(actual: CardToWithOwner, expected: CardToWithOwner) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "owner").isEqualTo(expected)
        if (actual.owner != null) {
            assertThat(expected.owner).isNotNull()
            assertMatch(actual.owner!!, expected.owner!!)
        }
    }

    fun assertMatch(actual: Iterable<Card>, expected: Iterable<Card>) {
        assertThat(actual).usingElementComparatorIgnoringFields("pinCode", "registered").isEqualTo(expected)
    }

    fun assertMatch(actual: Iterable<Card>, vararg expected: Card) {
        assertMatch(actual, expected.toList())
    }

    fun assertMatch(actual: CardTo, expected: CardTo) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    fun assertMatchTo(actual: Iterable<CardTo>, expected: Iterable<CardTo>) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    fun contentJson(expected: CardTo) = ResultMatcher {
        val actual = JsonUtil.OBJECT_MAPPER.readValue<CardTo>(JsonUtil.getContent(it))
        assertMatch(actual, expected)
    }

    fun contentJsonWithOwner(expected: CardToWithOwner) = ResultMatcher {
        val actual = JsonUtil.OBJECT_MAPPER.readValue<CardToWithOwner>(JsonUtil.getContent(it))
        assertMatch(actual, expected)
    }

    fun contentJsonList(vararg expected: CardTo) = ResultMatcher {
        val actual = JsonUtil.OBJECT_MAPPER.readValue<List<CardTo>>(JsonUtil.getContent(it))
        assertMatchTo(actual, expected.toList())
    }
}