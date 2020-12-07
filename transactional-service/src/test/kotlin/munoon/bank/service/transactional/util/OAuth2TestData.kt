package munoon.bank.service.transactional.util

import org.springframework.security.oauth2.provider.OAuth2Request

object OAuth2TestData {
    const val DEFAULT_CLIENT_ID = "web"
    val DEFAULT_OAUTH_REQUEST = OAuth2Request(
            mapOf("client_id" to DEFAULT_CLIENT_ID),
            DEFAULT_CLIENT_ID, emptySet(),
            true, setOf("user_info"),
            emptySet(), "http://localhost:8080",
            emptySet(), emptyMap()
    )
}