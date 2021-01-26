package munook.bank.service.market

import munook.bank.service.market.util.TestUtils
import munoon.bank.common.AuthorizedUser
import munoon.bank.common.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.RequestPostProcessor
import java.util.*
import org.mockito.Mockito.`when` as mockWhen

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = ["classpath:db/data.sql"])
abstract class AbstractTest

@AutoConfigureMockMvc
abstract class AbstractWebTest : AbstractTest() {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var tokenStore: TokenStore

    private fun getAccessToken(user: User = TestUtils.DEFAULT_USER,
                               request: OAuth2Request = TestUtils.DEFAULT_OAUTH_REQUEST,
                               token: String = UUID.randomUUID().toString()): OAuth2AccessToken {
        val authUser = AuthorizedUser(user)
        val authToken = UsernamePasswordAuthenticationToken(authUser, "N/A", authUser.authorities)
        val authentication = OAuth2Authentication(request, authToken)

        val accessToken = DefaultOAuth2AccessToken(token)
        mockWhen(tokenStore.readAccessToken(accessToken.value)).thenReturn(accessToken)
        mockWhen(tokenStore.readAuthentication(accessToken)).thenReturn(authentication)
        return accessToken
    }

    protected fun authUser(user: User = TestUtils.DEFAULT_USER,
                           request: OAuth2Request = TestUtils.DEFAULT_OAUTH_REQUEST,
                           token: String = UUID.randomUUID().toString()): RequestPostProcessor {
        val accessToken = getAccessToken(user, request, token)
        val headerToken = "${OAuth2AccessToken.BEARER_TYPE} ${accessToken.value}"
        return RequestPostProcessor {
            it.addHeader(HttpHeaders.AUTHORIZATION, headerToken)
            it
        }
    }
}