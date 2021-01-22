package munoon.bank.service.auth.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.CharEncoding
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JSONAuthenticationHandler(private val objectMapper: ObjectMapper)
    : AuthenticationSuccessHandler, AuthenticationFailureHandler {
    private var requestCache: RequestCache = HttpSessionRequestCache()

    override fun onAuthenticationSuccess(req: HttpServletRequest, resp: HttpServletResponse, authentication: Authentication) {
        val redirectUrl: String = requestCache.getRequest(req, resp)?.redirectUrl ?: req.contextPath + "/"
        val info = SuccessAuthenticationInfo(redirectUrl)
        writeResponse(resp, info, HttpStatus.OK)
    }

    override fun onAuthenticationFailure(req: HttpServletRequest, resp: HttpServletResponse, ex: AuthenticationException) {
        val errorCode: String? = when (ex) {
            is BadCredentialsException -> "bad_credentials"
            else -> null
        }
        val info = FailAuthenticationInfo(errorCode, ex.message)
        writeResponse(resp, info, HttpStatus.UNAUTHORIZED)
    }

    fun setRequestCache(requestCache: RequestCache) {
        this.requestCache = requestCache
    }

    private fun writeResponse(resp: HttpServletResponse, info: AuthenticationInfo, status: HttpStatus) {
        resp.characterEncoding = CharEncoding.UTF_8
        resp.contentType = MediaType.APPLICATION_JSON_VALUE
        resp.status = status.value()
        objectMapper.writeValue(resp.writer, info)
    }
}