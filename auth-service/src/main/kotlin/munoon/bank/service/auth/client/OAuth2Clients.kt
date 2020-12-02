package munoon.bank.service.auth.client

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@Component
@RefreshScope
@NoArgsConstructor
@ConfigurationProperties("oauth2")
class OAuth2Clients {
    lateinit var clients: List<ClientConfiguration>

    class ClientConfiguration {
        lateinit var id: String
        var secret: String? = null
        lateinit var redirectUri: Set<String>
        lateinit var authorizedGrantTypes: Set<String>
        var scopes: Set<String>? = null
        var autoApprove: Boolean = false
        var accessTokenValiditySeconds: Int? = null
        var refreshTokenValiditySeconds: Int? = null

        constructor()

        constructor(id: String, secret: String?, redirectUri: Set<String>, authorizedGrantTypes: Set<String>, scopes: Set<String>?, autoApprove: Boolean, accessTokenValiditySeconds: Int?, refreshTokenValiditySeconds: Int?) {
            this.id = id
            this.secret = secret
            this.redirectUri = redirectUri
            this.authorizedGrantTypes = authorizedGrantTypes
            this.scopes = scopes
            this.autoApprove = autoApprove
            this.accessTokenValiditySeconds = accessTokenValiditySeconds
            this.refreshTokenValiditySeconds = refreshTokenValiditySeconds
        }
    }
}