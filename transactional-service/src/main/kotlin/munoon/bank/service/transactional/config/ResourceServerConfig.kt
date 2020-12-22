package munoon.bank.service.transactional.config

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.auth.token.CustomJwtAccessTokenConverter
import munoon.bank.common.auth.user.TokenAuthenticationConverter
import munoon.bank.common.config.JsonConfig
import munoon.bank.service.transactional.user.UserService
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@EnableWebMvc
@Configuration
@EnableWebSecurity
@EnableResourceServer
@Import(JsonConfig::class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ResourceServerConfig : ResourceServerConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                .authorizeRequests()
                    .antMatchers("/microservices/**").permitAll()
                    .anyRequest().authenticated()
    }

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenStore(jwtTokenStore())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun accessTokenConverter(): AccessTokenConverter {
        val accessTokenConverter = DefaultAccessTokenConverter()
        accessTokenConverter.setUserTokenConverter(tokenAuthenticationConverter())
        return accessTokenConverter
    }

    @Bean
    fun customJwtTokenEnhancer(resource: ResourceServerProperties? = null): JwtAccessTokenConverter {
        val converter = CustomJwtAccessTokenConverter()
        converter.accessTokenConverter = accessTokenConverter()

        val keyValue = resource!!.jwt.keyValue
        if (keyValue.isNotBlank() && !keyValue.startsWith("-----BEGIN")) {
            converter.setSigningKey(keyValue)
        }
        if (keyValue != null) {
            converter.setVerifierKey(keyValue)
        }

        return converter
    }

    @Bean
    fun jwtTokenStore(): TokenStore = JwtTokenStore(customJwtTokenEnhancer())

    @Bean
    fun tokenAuthenticationConverter(userService: UserService? = null) = object : TokenAuthenticationConverter() {
        override fun getAuthorizedUser(token: String): AuthorizedUser {
            val userTo = userService!!.getProfileByToken(token)
            return AuthorizedUser(userTo)
        }
    }
}