package munoon.bank.service.transactional

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.user.User
import munoon.bank.service.transactional.user.UserTestData
import munoon.bank.service.transactional.util.OAuth2TestData
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*
import org.mockito.Mockito.`when` as mockWhen

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureStubRunner(
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        ids = ["munoon.bank:user-resource-service:+:stubs:8020"]
)
abstract class AbstractTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun init() {
        mongoTemplate.db.drop()
    }

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @MockBean
    private lateinit var tokenStore: TokenStore

    protected lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply<DefaultMockMvcBuilder>(springSecurity())
                .build()
    }

    private fun getAccessToken(user: User = UserTestData.DEFAULT_USER,
                               request: OAuth2Request = OAuth2TestData.DEFAULT_OAUTH_REQUEST,
                               token: String = UUID.randomUUID().toString()): OAuth2AccessToken {
        val authUser = AuthorizedUser(user)
        val authToken = UsernamePasswordAuthenticationToken(authUser, "N/A", authUser.authorities)
        val authentication = OAuth2Authentication(request, authToken)

        val accessToken = DefaultOAuth2AccessToken(token)
        mockWhen(tokenStore.readAccessToken(accessToken.value)).thenReturn(accessToken)
        mockWhen(tokenStore.readAuthentication(accessToken)).thenReturn(authentication)
        return accessToken
    }

    protected fun authUser(user: User = UserTestData.DEFAULT_USER,
                           request: OAuth2Request = OAuth2TestData.DEFAULT_OAUTH_REQUEST,
                           token: String = UUID.randomUUID().toString()): RequestPostProcessor {
        val accessToken = getAccessToken(user, request, token)
        val headerToken = "${OAuth2AccessToken.BEARER_TYPE} ${accessToken.value}"
        return RequestPostProcessor {
            it.addHeader(HttpHeaders.AUTHORIZATION, headerToken)
            it
        }
    }
}