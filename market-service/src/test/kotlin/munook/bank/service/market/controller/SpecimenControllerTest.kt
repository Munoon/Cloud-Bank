package munook.bank.service.market.controller

import munook.bank.service.market.AbstractWebTest
import munook.bank.service.market.product.ProductTestData.SNICKERS
import munook.bank.service.market.product.ProductTestData.SNICKERS_ID
import munook.bank.service.market.specimen.*
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1_ID
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_2
import munook.bank.service.market.specimen.SpecimenTestData.assertMatch
import munook.bank.service.market.specimen.SpecimenTestData.contentType
import munook.bank.service.market.specimen.SpecimenTestData.contentTypeList
import munook.bank.service.market.util.JsonUtil
import munook.bank.service.market.util.ResponseExceptionValidator.error
import munook.bank.service.market.util.ResponseExceptionValidator.fieldError
import munoon.bank.common.error.ErrorType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class SpecimenControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var specimenService: SpecimenService

    @Test
    fun createSpecimen() {
        val saveSpecimenTo = SaveSpecimenTo("snickers_id", SNICKERS_ID, true)
        val result = mockMvc.perform(post("/specimen")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveSpecimenTo)))
                .andExpect(status().isOk())
                .andReturn()

        val specimen = JsonUtil.readFromJson(result, SpecimenTo::class)
        val expected = Specimen(specimen.id, "snickers_id", SNICKERS, true, specimen.created)

        val actual = specimenService.getAllSpecimensByProduct(PageRequest.of(0, 10), SNICKERS_ID, false)
        assertMatch(actual.content, SNICKERS_1, SNICKERS_2, expected)
    }

    @Test
    fun createSpecimenProductNotFound() {
        val saveSpecimenTo = SaveSpecimenTo("snickers_id", 999, true)
        mockMvc.perform(post("/specimen")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveSpecimenTo)))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun createSpecimenInvalid() {
        val saveSpecimenTo = SaveSpecimenTo("", SNICKERS_ID, true)
        mockMvc.perform(post("/specimen")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveSpecimenTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("customId"))
    }

    @Test
    fun updateSpecimen() {
        val saveSpecimenTo = SaveSpecimenTo("snickers_id", SNICKERS_ID, false)
        val result = mockMvc.perform(put("/specimen/${SNICKERS_1_ID}")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveSpecimenTo)))
                .andExpect(status().isOk())
                .andReturn()

        val specimen = JsonUtil.readFromJson(result, SpecimenTo::class)
        val expected = Specimen(specimen.id, "snickers_id", SNICKERS, false, specimen.created)

        val actual = specimenService.getAllSpecimensByProduct(PageRequest.of(0, 10), SNICKERS_ID, false)
        assertMatch(actual.content, SNICKERS_2, expected)
    }

    @Test
    fun updateSpecimenNotFound() {
        val saveSpecimenTo = SaveSpecimenTo("snickers_id", SNICKERS_ID, true)
        mockMvc.perform(put("/specimen/999")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveSpecimenTo)))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun updateSpecimenProductNotFound() {
        val saveSpecimenTo = SaveSpecimenTo("snickers_id", 999, true)
        mockMvc.perform(put("/specimen/${SNICKERS_1_ID}")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveSpecimenTo)))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun updateSpecimenInvalid() {
        val saveSpecimenTo = SaveSpecimenTo("", SNICKERS_ID, false)
        mockMvc.perform(put("/specimen/${SNICKERS_1_ID}")
            .with(authUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(saveSpecimenTo)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(fieldError("customId"))
    }

    @Test
    fun getSpecimenById() {
        mockMvc.perform(get("/specimen/${SNICKERS_1_ID}")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentType(SNICKERS_1.asTo()))
    }

    @Test
    fun getSpecimenByIdNotFound() {
        mockMvc.perform(get("/specimen/999")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun getSpecimenByCustomId() {
        mockMvc.perform(get("/specimen/custom/${SNICKERS_1.customId}")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentType(SNICKERS_1.asTo()))
    }

    @Test
    fun getSpecimenByCustomIdNotFound() {
        mockMvc.perform(get("/specimen/custom/abc")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun getAllSpecimen() {
        mockMvc.perform(get("/specimen")
                .param("page", "0")
                .param("size", "10")
                .param("productId", SNICKERS_ID.toString())
                .param("ableToBuyOnly", "false")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentTypeList(SNICKERS_1.asTo(), SNICKERS_2.asTo()))
    }

    @Test
    fun getAllSpecimenAbleToBuyOnly() {
        mockMvc.perform(get("/specimen")
                .param("page", "0")
                .param("size", "10")
                .param("productId", SNICKERS_ID.toString())
                .param("ableToBuyOnly", "true")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentTypeList(SNICKERS_1.asTo()))
    }
}