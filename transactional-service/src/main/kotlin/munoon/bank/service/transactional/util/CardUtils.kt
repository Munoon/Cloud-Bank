package munoon.bank.service.transactional.util

import munoon.bank.service.transactional.card.Card
import org.springframework.security.access.AccessDeniedException

object CardUtils {
    fun checkCardOwner(userId: Int, card: Card, message: String = "This card belong to other user") {
        if (card.userId != userId) {
            throw AccessDeniedException(message)
        }
    }

    fun checkCardActive(card: Card, message: String = "This card is not active") {
        if (!card.active) {
            throw AccessDeniedException(message)
        }
    }
}