package munoon.bank.service.transactional.config

import feign.RequestInterceptor
import munoon.bank.common.util.MicroserviceUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class DefaultFeignConfig {
    @Bean
    fun listToStringCommaRequestInterceptor() = RequestInterceptor {
        val queries = it.queries().entries.map { entry ->
            entry.key to if (entry.value.size > 1) {
                val query = entry.value.joinToString(",")
                listOf(query)
            } else entry.value
        }.toMap()

        it.queries(null)
        it.queries(queries)
    }

    @Bean
    fun microserviceNameToHeaderRequestInterceptor(@Value("\${spring.application.name:transactional-service}") applicationName: String) =
            RequestInterceptor {
                it.header(MicroserviceUtils.MICROSERVICE_HEADER_NAME, applicationName)
            }
}
