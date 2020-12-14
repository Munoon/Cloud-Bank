package munoon.bank.service.transactional.card

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@Component
@RefreshScope
@ConfigurationProperties("application")
class CardsProperties {
    lateinit var cards: Map<String, CardProperties>

    class CardProperties {
        lateinit var name: String
        var price: Double = 0.0
        var ableToBuy: Boolean = false
        var clientLimit: Int? = null
        var tax = Tax()

        class Tax {
            var award: Double = 0.0
            var fine: Double = 0.0
            var other: Double = 0.0
        }
    }
}