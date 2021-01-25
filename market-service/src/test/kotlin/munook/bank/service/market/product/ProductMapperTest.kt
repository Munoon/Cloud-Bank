package munook.bank.service.market.product

import munook.bank.service.market.product.ProductTestData.assertMatch
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProductMapperTest {
    @Test
    fun update() {
        val product = Product(100, "Name", ProductType.UNFIXED_SPECIMEN, null, 10.0, true, LocalDateTime.now())
        val expected = Product(100, "test", ProductType.FIXED_SPECIMEN, 10, 15.0, false, product.created)
        val saveProductTo = SaveProductTo("test", ProductType.FIXED_SPECIMEN, 10, 15.0, false)
        assertMatch(ProductMapper.INSTANCE.update(saveProductTo, product), expected)
    }

    @Test
    fun create() {
        val actual = ProductMapper.INSTANCE.create(SaveProductTo("test", ProductType.FIXED_SPECIMEN, 10, 15.0, true))
        val expected = Product(null, "test", ProductType.FIXED_SPECIMEN, 10, 15.0, true, actual.created)
        assertMatch(actual, expected)
    }
}