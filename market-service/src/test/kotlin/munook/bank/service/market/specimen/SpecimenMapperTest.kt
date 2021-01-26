package munook.bank.service.market.specimen

import munook.bank.service.market.product.ProductTestData.SNICKERS
import munook.bank.service.market.product.ProductTestData.TWIX
import munook.bank.service.market.product.asTo
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1_ID
import munook.bank.service.market.specimen.SpecimenTestData.assertMatch
import org.junit.jupiter.api.Test

class SpecimenMapperTest {
    @Test
    fun create() {
        val actual = SpecimenMapper.INSTANCE.create(SaveSpecimenTo("snickers_id", 100, true), SNICKERS)
        val expected = Specimen(null, "snickers_id", SNICKERS, true, actual.created)
        assertMatch(actual, expected)
    }

    @Test
    fun update() {
        val actual = SpecimenMapper.INSTANCE.update(SaveSpecimenTo("twix_id", 101, false), TWIX, SNICKERS_1.copy())
        val expected = Specimen(SNICKERS_1_ID, "twix_id", TWIX, false, SNICKERS_1.created)
        assertMatch(actual, expected)
    }

    @Test
    fun asTo() {
        val specimen = Specimen(SNICKERS_1_ID, "twix_id", TWIX, true, SNICKERS_1.created)
        val expected = SpecimenTo(SNICKERS_1_ID, "twix_id", TWIX.asTo(), true, SNICKERS_1.created)
        assertMatch(SpecimenMapper.INSTANCE.asTo(specimen), expected)
    }
}