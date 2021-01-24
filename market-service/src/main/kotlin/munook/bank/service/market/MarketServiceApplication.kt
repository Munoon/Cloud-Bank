package munook.bank.service.market

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class MarketServiceApplication

fun main(args: Array<String>) {
    runApplication<MarketServiceApplication>(*args)
}
