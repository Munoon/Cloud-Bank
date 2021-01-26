package munook.bank.service.market.specimen

import com.fasterxml.jackson.module.kotlin.readValue
import munook.bank.service.market.product.ProductTestData.SNICKERS
import munook.bank.service.market.product.ProductTestData.assertMatch
import munook.bank.service.market.util.JsonUtil
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher
import java.time.LocalDateTime

object SpecimenTestData {
    const val SNICKERS_1_ID = 100
    const val SNICKERS_1_CUSTOM_ID = "snickers_1"
    val SNICKERS_1 = Specimen(SNICKERS_1_ID, SNICKERS_1_CUSTOM_ID, SNICKERS, true, LocalDateTime.now())
    val SNICKERS_2 = Specimen(SNICKERS_1_ID + 1, "snickers_2", SNICKERS, false, LocalDateTime.now())

    fun assertMatch(actual: Specimen, expected: Specimen) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("created", "product").isEqualTo(expected)
        assertMatch(actual.product, expected.product)
    }

    fun assertMatch(actual: Iterable<Specimen>, vararg expected: Specimen) {
        assertThat(actual).usingElementComparatorIgnoringFields("created", "product").isEqualTo(expected.toList())
        actual.forEachIndexed { index, specimen -> assertMatch(specimen.product, expected[index].product) }
    }

    fun assertMatch(actual: SpecimenTo, expected: SpecimenTo) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("created", "product").isEqualTo(expected)
        assertMatch(actual.product, expected.product)
    }

    fun assertMatch(actual: Iterable<SpecimenTo>, vararg expected: SpecimenTo) {
        assertThat(actual).usingElementComparatorIgnoringFields("created", "product").isEqualTo(expected.toList())
        actual.forEachIndexed { index, specimen -> assertMatch(specimen.product, expected[index].product) }
    }

    fun contentType(expected: SpecimenTo) = ResultMatcher {
        val actual = JsonUtil.readFromJson(it, SpecimenTo::class)
        assertMatch(actual, expected)
    }

    fun contentTypeList(vararg expected: SpecimenTo) = ResultMatcher {
        val node = JsonUtil.OBJECT_MAPPER.readTree(JsonUtil.getContent(it)).at("/content")
        val actual = JsonUtil.OBJECT_MAPPER.readValue<List<SpecimenTo>>(node.toString())
        assertMatch(actual, *expected)
    }
}