package munoon.bank.service.resource.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class UserResourceServiceApplication

fun main(args: Array<String>) {
    runApplication<UserResourceServiceApplication>(*args)
}
