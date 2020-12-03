package munoon.bank.service.resource.user.util

import munoon.bank.common.error.ErrorInfoField
import munoon.bank.common.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object ResponseExceptionValidator {
    fun fieldError(field: String) = ResultMatcher {
        val errorInfo = JsonUtils.readFromJson(it, ErrorInfoField::class.java)
        assertThat(errorInfo.errorType).isEqualTo(ErrorType.VALIDATION_ERROR)
        assertThat(errorInfo.fields[field]).isNotNull()
    }
}