package munoon.bank.service.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import munoon.bank.service.auth.authentication.AuthenticationInfo
import munoon.bank.service.auth.authentication.AuthenticationStatus
import munoon.bank.service.auth.authentication.FailAuthenticationInfo
import munoon.bank.service.auth.authentication.SuccessAuthenticationInfo
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher

object JsonTestUtils {
    private val OBJECT_MAPPER = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    fun authInfoJson(expected: AuthenticationInfo) = ResultMatcher {
        val content = it.response.contentAsString
        val jsonNode = OBJECT_MAPPER.readTree(content)
        val status = jsonNode.get("status").asText()
        val clazz = when (AuthenticationStatus.valueOf(status)) {
            AuthenticationStatus.SUCCESS -> SuccessAuthenticationInfo::class.java
            AuthenticationStatus.FAIL -> FailAuthenticationInfo::class.java
        }
        val actual = OBJECT_MAPPER.readValue(content, clazz)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}