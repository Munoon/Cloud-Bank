package munoon.bank.service.resource.user.config

import feign.RequestInterceptor
import munoon.bank.common.util.MicroserviceUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients("munoon.bank.service.resource.user.client")
class FeignConfig {
    @Bean
    fun microserviceNameToHeaderRequestInterceptor(
            @Value("\${spring.application.name:user-resource-service}") applicationName: String
    ) = RequestInterceptor {
        it.header(MicroserviceUtils.MICROSERVICE_HEADER_NAME, applicationName)
    }
}