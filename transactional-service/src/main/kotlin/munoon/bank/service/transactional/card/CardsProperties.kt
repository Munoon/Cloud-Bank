package munoon.bank.service.transactional.card

import lombok.NoArgsConstructor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@Component
@RefreshScope
@NoArgsConstructor
@ConfigurationProperties("application")
class CardsProperties {
    lateinit var cards: List<CardProperties>

    @NoArgsConstructor
    class CardProperties {
        lateinit var name: String
        lateinit var codeName: String
        var price: Double = 0.0
        var ableToBuy: Boolean = false
        var clientLimit: Int? = null
    }
}