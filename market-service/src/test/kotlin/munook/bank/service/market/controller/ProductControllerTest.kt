package munook.bank.service.market.controller

import munook.bank.service.market.AbstractWebTest
import munook.bank.service.market.product.*
import munook.bank.service.market.product.ProductTestData.BOUNTY
import munook.bank.service.market.product.ProductTestData.SNICKERS
import munook.bank.service.market.product.ProductTestData.TWIX
import munook.bank.service.market.product.ProductTestData.assertMatch
import munook.bank.service.market.product.ProductTestData.contentType
import munook.bank.service.market.product.ProductTestData.contentTypeList
import munook.bank.service.market.specimen.SpecimenTestData.SNICKERS_1_ID
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

internal class ProductControllerTest : AbstractWebTest() {
    @Autowired
    private lateinit var productService: ProductService

    @Test
    fun createProduct() {
        val saveProductTo = SaveProductTo("test product", ProductType.UNFIXED_SPECIMEN, 10, 15.0, true)
        val result = mockMvc.perform(post("/product")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveProductTo)))
                .andExpect(status().isOk())
                .andReturn()

        val product = JsonUtil.readFromJson(result, ProductTo::class)
        val expected = Product(product.id, "test product", ProductType.UNFIXED_SPECIMEN, 10, 15.0, true, product.created)

        val actual = productService.getProducts(PageRequest.of(0, 10), false)
        assertMatch(actual, SNICKERS, TWIX, BOUNTY, expected)
    }

    @Test
    fun createProductInvalid() {
        val saveProductTo = SaveProductTo("", ProductType.UNFIXED_SPECIMEN, -1, 15.001, true)
        mockMvc.perform(post("/product")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveProductTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("name", "count", "price"))
    }

    @Test
    fun updateProduct() {
        val saveProductTo = SaveProductTo("test product", ProductType.UNFIXED_SPECIMEN, 10, 15.0, true)
        val result = mockMvc.perform(put("/product/${SNICKERS_1_ID}")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveProductTo)))
                .andExpect(status().isOk())
                .andReturn()

        val product = JsonUtil.readFromJson(result, ProductTo::class)
        val expected = Product(product.id, "test product", ProductType.UNFIXED_SPECIMEN, 10, 15.0, true, product.created)

        val actual = productService.getProducts(PageRequest.of(0, 10), false)
        assertMatch(actual.content, TWIX, BOUNTY, expected)
    }

    @Test
    fun updateProductNotFound() {
        val saveProductTo = SaveProductTo("test product", ProductType.UNFIXED_SPECIMEN, 10, 15.0, true)
        mockMvc.perform(put("/product/999")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveProductTo)))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun updateProductInvalid() {
        val saveProductTo = SaveProductTo("", ProductType.UNFIXED_SPECIMEN, -1, 15.001, true)
        mockMvc.perform(put("/product/${SNICKERS_1_ID}")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(saveProductTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(fieldError("name", "count", "price"))
    }

    @Test
    fun getProductById() {
        mockMvc.perform(get("/product/${SNICKERS_1_ID}")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentType(SNICKERS.asTo()))
    }

    @Test
    fun getProductByIdNotFound() {
        mockMvc.perform(get("/product/999")
                .with(authUser()))
                .andExpect(status().isNotFound())
                .andExpect(error(ErrorType.NOT_FOUND))
    }

    @Test
    fun getProducts() {
        mockMvc.perform(get("/product")
                .param("page", "0")
                .param("size", "10")
                .param("ableToBuyOnly", "false")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentTypeList(SNICKERS.asTo(), TWIX.asTo(), BOUNTY.asTo()))
    }

    @Test
    fun getProductsAbleToBuyOnly() {
        mockMvc.perform(get("/product")
                .param("page", "0")
                .param("size", "10")
                .param("ableToBuyOnly", "true")
                .with(authUser()))
                .andExpect(status().isOk())
                .andExpect(contentTypeList(SNICKERS.asTo(), TWIX.asTo()))
    }
}