package munoon.bank.service.auth.config

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.CustomUserAuthenticationConverter
import munoon.bank.common.user.User
import munoon.bank.service.auth.client.CustomClientDetailsService
import munoon.bank.service.auth.user.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.security.KeyPair

@Configuration
@EnableAuthorizationServer
class AuthServerConfig(private val clientDetailsService: CustomClientDetailsService,
                       private val userService: UserService,
                       private val jwtCertificateProperties: JwtCertificateProperties)
    : AuthorizationServerConfigurerAdapter() {

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.withClientDetails(clientDetailsService)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints
                .userDetailsService(userService)
                .accessTokenConverter(jwtAccessTokenConverter())
    }

    @Bean
    fun jwtAccessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.accessTokenConverter = accessTokenConverter()
        converter.setKeyPair(keyPair())
        return converter
    }

    @Bean
    fun accessTokenConverter(): AccessTokenConverter {
        val accessTokenConverter = DefaultAccessTokenConverter()
        accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter())
        return accessTokenConverter
    }

    @Bean
    fun customUserAuthenticationConverter(): CustomUserAuthenticationConverter {
        return object : CustomUserAuthenticationConverter() {
            override fun getAuthorizedUser(userId: Int): AuthorizedUser? {
                val user: User = userService.getById(userId)
                return AuthorizedUser(user)
            }
        }
    }

    @Bean
    fun keyPair(): KeyPair {
        val keyStoreKeyFactory = KeyStoreKeyFactory(
                jwtCertificateProperties.store!!.file,
                jwtCertificateProperties.store!!.password.toCharArray()
        )
        return keyStoreKeyFactory.getKeyPair(
                jwtCertificateProperties.key!!.alias,
                jwtCertificateProperties.key!!.password.toCharArray()
        )
    }
}