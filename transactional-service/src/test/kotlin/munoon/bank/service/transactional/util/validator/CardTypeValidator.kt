package munoon.bank.service.transactional.util.validator

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.util.validation.CardType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.validation.Validator

class CardTypeValidator : AbstractTest() {
    @Autowired
    private lateinit var validator: Validator

    @Test
    fun testValid() {
        val data = TestData("default")
        val validate = validator.validate(data)
        assertThat(validate).hasSize(0)
    }

    @Test
    fun testInvalid() {
        val data = TestData("abc")
        val validate = validator.validate(data)
        assertThat(validate).hasSize(1)
    }

    private data class TestData(
            @field:CardType
            val type: String
    )
}