package munoon.bank.service.resource.user.util.validator

import munoon.bank.service.resource.user.AbstractTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.validation.Validator

internal class ValidClassTest : AbstractTest() {
    @Autowired
    private lateinit var validator: Validator

    @Test
    fun testValid() {
        val data = TestData("10")
        val result = validator.validate(data)
        Assertions.assertThat(result).hasSize(0)
    }

    @Test
    fun testInvalid() {
        val data = TestData("abc")
        val result = validator.validate(data)
        Assertions.assertThat(result).hasSize(1)
    }

    private data class TestData(
            @field:ValidClass
            val clazz: String
    )
}