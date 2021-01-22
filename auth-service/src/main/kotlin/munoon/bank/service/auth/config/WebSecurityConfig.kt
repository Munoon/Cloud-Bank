package munoon.bank.service.auth.config

import com.fasterxml.jackson.databind.ObjectMapper
import munoon.bank.service.auth.authentication.JSONAuthenticationHandler
import munoon.bank.service.auth.user.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.savedrequest.RequestCache

@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val userService: UserService) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                    .antMatchers("/login**").anonymous()
                    .antMatchers("/static/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .csrf().and()
                .logout()
                    .permitAll()
                    .and()
                .formLogin()
                    .successHandler(jsonAuthenticationHandler())
                    .failureHandler(jsonAuthenticationHandler())
                    .loginPage("/login")

        val requestCache = http.getSharedObject(RequestCache::class.java)
        if (requestCache != null) {
            jsonAuthenticationHandler().setRequestCache(requestCache)
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService)
    }

    @Bean
    fun jsonAuthenticationHandler(objectMapper: ObjectMapper? = null) = JSONAuthenticationHandler(objectMapper!!)
}