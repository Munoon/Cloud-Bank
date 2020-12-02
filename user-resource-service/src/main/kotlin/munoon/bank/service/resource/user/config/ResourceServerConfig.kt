package munoon.bank.service.resource.user.config

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.CustomUserAuthenticationConverter
import munoon.bank.service.resource.user.user.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter

@Configuration
@EnableWebSecurity
@EnableResourceServer
class ResourceServerConfig : ResourceServerConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                .authorizeRequests()
                    .anyRequest().authenticated()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun accessTokenConverter(jwtAccessTokenConverter: JwtAccessTokenConverter): AccessTokenConverter {
        val accessTokenConverter = DefaultAccessTokenConverter()
        accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter(null))
        jwtAccessTokenConverter.accessTokenConverter = accessTokenConverter
        return accessTokenConverter
    }

    @Bean
    fun customUserAuthenticationConverter(userService: UserService?): CustomUserAuthenticationConverter {
        return object : CustomUserAuthenticationConverter() {
            override fun getAuthorizedUser(userId: Int): AuthorizedUser {
                val user = userService!!.getById(userId)
                return AuthorizedUser(user)
            }
        }
    }
}