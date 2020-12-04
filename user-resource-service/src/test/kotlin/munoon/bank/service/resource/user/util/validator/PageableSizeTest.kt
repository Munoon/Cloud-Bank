package munoon.bank.service.resource.user.util.validator

import munoon.bank.service.resource.user.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import javax.validation.Validator

internal class PageableSizeTest : AbstractTest() {
    @Autowired
    private lateinit var validator: Validator

    @Test
    fun testValid() {
        val data = TestData(PageRequest.of(0, 15))
        val result = validator.validate(data)
        assertThat(result).hasSize(0)
    }

    @Test
    fun testInvalidLess() {
        val data = TestData(PageRequest.of(0, 5))
        val result = validator.validate(data)
        assertThat(result).hasSize(1)
    }

    @Test
    fun testInvalidMore() {
        val data = TestData(PageRequest.of(0, 25))
        val result = validator.validate(data)
        assertThat(result).hasSize(1)
    }

    private data class TestData(
            @field:PageableSize(min = 10, max = 20)
            val pageable: Pageable
    )
}