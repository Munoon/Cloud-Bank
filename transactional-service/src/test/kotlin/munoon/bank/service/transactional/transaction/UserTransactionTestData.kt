package munoon.bank.service.transactional.transaction

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import munoon.bank.service.transactional.util.JsonUtil
import munoon.bank.service.transactional.util.JsonUtil.getContent
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object UserTransactionTestData {
    fun assertMatch(actual: UserTransactionTo, expected: UserTransactionTo) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("card", "info", "registered").isEqualTo(expected)
        assertThat(actual.card).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected.card)
        assertThat(actual.info).usingRecursiveComparison().ignoringFields("buyCard.registered").isEqualTo(expected.info)
    }

    fun assertMatch(actual: UserTransaction, expected: UserTransaction) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("card.registered", "card.pinCode", "info.buyCard.pinCode", "info.buyCard.registered", "registered")
                .isEqualTo(expected)
    }

    fun assertMatch(actual: SafeUserTransactionTo, expected: SafeUserTransactionTo) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    fun assertMatchTo(actual: List<UserTransactionTo>, expected: List<UserTransactionTo>) {
        assertThat(actual).hasSize(expected.size)
        actual.forEachIndexed { index, it -> assertMatch(it, expected[index]) }
    }

    fun assertMatch(actual: List<UserTransactionTo>, vararg expected: UserTransactionTo) {
        assertMatchTo(actual, expected.toList())
    }

    fun assertMatch(actual: List<UserTransaction>, expected: List<UserTransaction>) {
        assertThat(actual).hasSize(expected.size)
        actual.forEachIndexed { index, it -> assertMatch(it, expected[index]) }
    }

    fun assertMatch(actual: List<UserTransaction>, vararg expected: UserTransaction) {
        assertMatch(actual, expected.toList())
    }

    fun contentJson(expected: UserTransactionTo) = ResultMatcher {
        val actual = readFromJson(getContent(it))
        assertMatch(actual, expected)
    }

    fun contentJsonList(vararg expected: UserTransactionTo) = ResultMatcher {
        val actual = readFromJsonList(getContent(it))
        assertMatchTo(actual, expected.toList())
    }

    fun readFromJsonList(json: String): List<UserTransactionTo> = JsonUtil.OBJECT_MAPPER.readTree(json)
            .at("/content")
            .map { readFromJson(it.toString()) }

    fun readFromJson(json: String): UserTransactionTo {
        val objectNode = (JsonUtil.OBJECT_MAPPER.readTree(json) as ObjectNode)
        val info = objectNode.remove("info").toString()
        val result = JsonUtil.OBJECT_MAPPER.readValue<UserTransactionTo>(objectNode.toString())
        return result.copy(info = getTransactionInfo(result.type, info))
    }

    private fun getTransactionInfo(type: UserTransactionType, info: String) = when (type) {
        UserTransactionType.CARD_BUY -> JsonUtil.OBJECT_MAPPER.readValue<BuyCardUserTransactionInfoTo>(info)
        UserTransactionType.AWARD -> JsonUtil.OBJECT_MAPPER.readValue<AwardUserTransactionInfoTo>(info)
        UserTransactionType.FINE -> JsonUtil.OBJECT_MAPPER.readValue<FineUserTransactionInfoTo>(info)
        UserTransactionType.RECEIVE_MONEY -> JsonUtil.OBJECT_MAPPER.readValue<ReceiveUserTransactionInfoTo>(info)
        UserTransactionType.TRANSLATE_MONEY -> JsonUtil.OBJECT_MAPPER.readValue<TranslateUserTransactionInfoTo>(info)
        UserTransactionType.SALARY -> null
    }
}