package munook.bank.service.market.product

import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

object ProductTestData {
    const val SNICKERS_ID = 100
    val SNICKERS = Product(SNICKERS_ID, "Snickers", ProductType.FIXED_SPECIMEN, null, 10.0, true, LocalDateTime.now())
    val TWIX = Product(SNICKERS_ID + 1, "Twix", ProductType.UNFIXED_SPECIMEN, 10, 15.0, true, LocalDateTime.now())
    val BOUNTY = Product(SNICKERS_ID + 2, "Bounty", ProductType.UNFIXED_SPECIMEN, 20, 20.0, false, LocalDateTime.now())

    fun assertMatch(actual: Product, expected: Product) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("created").isEqualTo(expected)
    }

    fun assertMatch(actual: Iterable<Product>, vararg expected: Product) {
        assertThat(actual).usingElementComparatorIgnoringFields("created").isEqualTo(expected.toList())
    }
}