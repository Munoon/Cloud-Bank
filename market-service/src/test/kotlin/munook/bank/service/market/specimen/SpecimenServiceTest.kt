package munook.bank.service.market.specimen

import munook.bank.service.market.AbstractTest
import munook.bank.service.market.product.ProductTestData.SNICKERS
import munook.bank.service.market.product.ProductTestData.SNICKERS_ID
import munook.bank.service.market.product.ProductTestData.TWIX
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1_CUSTOM_ID
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1_ID
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_2
import munook.bank.service.market.specimen.SpecimenTestData.assertMatch
import munoon.bank.common.util.exception.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

internal class SpecimenServiceTest : AbstractTest() {
    @Autowired
    private lateinit var specimenService: SpecimenService

    @Test
    fun createSpecimen() {
        val specimen = specimenService.createSpecimen(SaveSpecimenTo("snickers_3", 100, true))
        val expected = Specimen(specimen.id, "snickers_3", SNICKERS, true, specimen.created)
        val actual = specimenService.getAllSpecimensByProduct(PageRequest.of(0, 10), SNICKERS_ID, true)
        assertMatch(actual, SNICKERS_1, expected)
    }

    @Test
    fun createSpecimenProductNotFound() {
        assertThrows<NotFoundException> {
            specimenService.createSpecimen(SaveSpecimenTo("specimen_id", 999, true))
        }
    }

    @Test
    fun updateSpecimen() {
        val specimen = specimenService.updateSpecimen(SNICKERS_1_ID, SaveSpecimenTo("twix_specimen", TWIX.id!!, false))
        val expected = Specimen(SNICKERS_1_ID, "twix_specimen", TWIX, false, specimen.created)
        val actual = specimenService.getAllSpecimensByProduct(PageRequest.of(0, 10), TWIX.id!!, false)
        assertMatch(actual, expected)
    }

    @Test
    fun updateSpecimenNotFound() {
        assertThrows<NotFoundException> {
            specimenService.updateSpecimen(999, SaveSpecimenTo("id", SNICKERS_ID, false))
        }
    }

    @Test
    fun updateSpecimenProductNotFound() {
        assertThrows<NotFoundException> {
            specimenService.updateSpecimen(SNICKERS_1_ID, SaveSpecimenTo("id", 999, false))
        }
    }

    @Test
    fun getSpecimenById() {
        val actual = specimenService.getSpecimenById(SNICKERS_1_ID)
        assertMatch(actual, SNICKERS_1)
    }

    @Test
    fun getSpecimenByIdNotFound() {
        assertThrows<NotFoundException> {
            specimenService.getSpecimenById(999)
        }
    }

    @Test
    fun getSpecimenByCustomId() {
        val actual = specimenService.getSpecimenByCustomId(SNICKERS_1_CUSTOM_ID)
        assertMatch(actual, SNICKERS_1)
    }

    @Test
    fun getSpecimenByCustomIdNotFound() {
        assertThrows<NotFoundException> {
            specimenService.getSpecimenByCustomId("abc")
        }
    }

    @Test
    fun getAllSpecimensByProduct() {
        val actual = specimenService.getAllSpecimensByProduct(PageRequest.of(0, 10), SNICKERS_ID, false)
        assertMatch(actual.content, SNICKERS_1, SNICKERS_2)
    }

    @Test
    fun getAllSpecimensByProductAbleToBuyOnly() {
        val actual = specimenService.getAllSpecimensByProduct(PageRequest.of(0, 10), SNICKERS_ID, true)
        assertMatch(actual.content, SNICKERS_1)
    }
}