package munoon.bank.service.transactional.config

import feign.RequestInterceptor
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
}
