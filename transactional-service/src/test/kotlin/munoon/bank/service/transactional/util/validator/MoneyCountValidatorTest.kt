package munoon.bank.service.transactional.util.validator

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.util.validation.CardType
import munoon.bank.service.transactional.util.validation.ValidMoneyCount
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import javax.validation.Validator

internal class MoneyCountValidatorTest : AbstractTest() {
    @Autowired
    private lateinit var validator: Validator

    @Test
    fun isValid() {
        val data = TestData(1.0)
        val result = validator.validate(data)
        assertThat(result).hasSize(0)
    }

    @Test
    fun testInvalid() {
        val data = TestData(1.00001)
        val result = validator.validate(data)
        assertThat(result).hasSize(1)
    }

    @Test
    fun testNull() {
        val data = TestData(null)
        val result = validator.validate(data)
        assertThat(result).hasSize(0)
    }

    private data class TestData(
            @field:ValidMoneyCount
            val count: Double?
    )
}