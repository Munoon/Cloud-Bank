package munook.bank.service.market.config

import com.fasterxml.jackson.databind.ObjectMapper
import munoon.bank.common.AuthorizedUser
import munoon.bank.common.auth.user.UserIdAuthenticationConverter
import munoon.bank.common.config.JsonConfig
import munoon.bank.common.user.UserTo
import munoon.bank.common.util.MicroserviceUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
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

    @Bean
    fun accessTokenConverter(jwtAccessTokenConverter: JwtAccessTokenConverter) = DefaultAccessTokenConverter().also {
        it.setUserTokenConverter(userIdAuthenticationConverter())
        jwtAccessTokenConverter.accessTokenConverter = it
    }

    @Bean
    fun userIdAuthenticationConverter(restTemplate: RestTemplate? = null) =
        object : UserIdAuthenticationConverter() {
            override fun getAuthorizedUser(userId: Int): AuthorizedUser {
                val users = restTemplate!!.getForObject<Array<UserTo>>("http://user-resource-service/microservices/users?ids=$userId")
                return AuthorizedUser(users[0])
            }
        }

    @Bean
    @LoadBalanced
    fun restTemplate(@Value("\${spring.application.name:transactional-service}") applicationName: String,
                     objectMapper: ObjectMapper): RestTemplate =
        RestTemplateBuilder({
            it.messageConverters = listOf(MappingJackson2HttpMessageConverter(objectMapper))
            it.interceptors.add(ClientHttpRequestInterceptor { req, body, execution ->
                req.headers.add(MicroserviceUtils.MICROSERVICE_HEADER_NAME, applicationName)
                req.headers.contentType = MediaType.APPLICATION_JSON
                execution.execute(req, body)
            })
        }).build()
}