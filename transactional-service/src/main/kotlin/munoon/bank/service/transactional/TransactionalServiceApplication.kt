package munoon.bank.service.transactional

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class TransactionalServiceApplication

fun main(args: Array<String>) {
    runApplication<TransactionalServiceApplication>(*args)
}