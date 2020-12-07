package munoon.bank.service.transactional.util

import munoon.bank.common.error.ErrorInfo
import munoon.bank.common.error.ErrorInfoField
import munoon.bank.common.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object ResponseExceptionValidator {
    fun fieldError(vararg fields: String) = ResultMatcher {
        val errorInfo = JsonUtil.readFromJson(it, ErrorInfoField::class.java)
        assertThat(errorInfo.errorType).isEqualTo(ErrorType.VALIDATION_ERROR)
        assertThat(errorInfo.fields.keys).isEqualTo(fields.toSet())
    }

    fun error(errorType: ErrorType) = ResultMatcher {
        val errorInfo = JsonUtil.readFromJson(it, ErrorInfo::class.java)
        assertThat(errorInfo.errorType).isEqualTo(errorType)
    }
}