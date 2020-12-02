package munoon.bank.service.auth.client

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.ClientDetails

data class CustomClientDetails(
        private val clientId: String,
        private val resourceIds: Set<String>,
        private val clientSecret: String?,
        private val scope: Set<String>?,
        private val authorizedGrantTypes: Set<String>,
        private val registeredRedirectUri: Set<String>,
        private val authorities: Collection<GrantedAuthority>,
        private val accessTokenValiditySeconds: Int?,
        private val refreshTokenValiditySeconds: Int?,
        private val alwaysAutoApprove: Boolean,
        private val additionalInformation: Map<String, Any>
) : ClientDetails {
    constructor(c: OAuth2Clients.ClientConfiguration) :
            this(
                    c.id, emptySet(), c.secret, c.scopes,
                    c.authorizedGrantTypes, c.redirectUri,
                    emptyList(),
                    c.accessTokenValiditySeconds, c.refreshTokenValiditySeconds,
                    c.autoApprove, emptyMap()
            )

    override fun isSecretRequired() = clientSecret != null
    override fun isScoped() = !scope.isNullOrEmpty()

    override fun getClientId() = clientId
    override fun getResourceIds() = resourceIds
    override fun getClientSecret() = clientSecret
    override fun getScope() = scope
    override fun getAuthorizedGrantTypes() = authorizedGrantTypes
    override fun getRegisteredRedirectUri() = registeredRedirectUri
    override fun getAuthorities() = authorities
    override fun getAccessTokenValiditySeconds() = accessTokenValiditySeconds
    override fun getRefreshTokenValiditySeconds() = refreshTokenValiditySeconds
    override fun isAutoApprove(scope: String?) = alwaysAutoApprove
    override fun getAdditionalInformation() = additionalInformation
}