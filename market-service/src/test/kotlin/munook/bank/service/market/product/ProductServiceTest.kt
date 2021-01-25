package munook.bank.service.market.product

import munook.bank.service.market.AbstractTest
import munook.bank.service.market.product.ProductTestData.BOUNTY
import munook.bank.service.market.product.ProductTestData.SNICKERS
import munook.bank.service.market.product.ProductTestData.SNICKERS_ID
import munook.bank.service.market.product.ProductTestData.TWIX
import munook.bank.service.market.product.ProductTestData.assertMatch
import munoon.bank.common.util.exception.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

internal class ProductServiceTest : AbstractTest() {
    @Autowired
    private lateinit var productService: ProductService

    @Test
    fun createProduct() {
        val created = productService.createProduct(SaveProductTo("test", ProductType.UNFIXED_SPECIMEN, 30, 25.0, true))
        val expected = Product(created.id, "test", ProductType.UNFIXED_SPECIMEN, 30, 25.0, true, created.created)
        assertMatch(productService.getProducts(PageRequest.of(0, 10), false), SNICKERS, TWIX, BOUNTY, expected)
    }

    @Test
    fun updateProduct() {
        val updated = productService.updateProduct(
            SNICKERS_ID,
            SaveProductTo("test", ProductType.UNFIXED_SPECIMEN, 30, 25.0, false)
        )
        val expected = Product(SNICKERS_ID, "test", ProductType.UNFIXED_SPECIMEN, 30, 25.0, false, updated.created)
        assertMatch(productService.getProductById(SNICKERS_ID), expected)
    }

    @Test
    fun updateProductNotFound() {
        assertThrows<NotFoundException> {
            productService.updateProduct(999, SaveProductTo("test", ProductType.UNFIXED_SPECIMEN, 30, 25.0, false))
        }
    }

    @Test
    fun getProductById() {
        assertMatch(productService.getProductById(SNICKERS_ID), SNICKERS)
    }

    @Test
    fun getProductByIdNotFound() {
        assertThrows<NotFoundException> {
            productService.getProductById(999)
        }
    }

    @Test
    fun getProductsAbleToBuy() {
        val actual = productService.getProducts(PageRequest.of(0, 10), true)
        assertMatch(actual, SNICKERS, TWIX)
    }

    @Test
    fun getProductsAll() {
        val actual = productService.getProducts(PageRequest.of(0, 10), false)
        assertMatch(actual, SNICKERS, TWIX, BOUNTY)
    }
}