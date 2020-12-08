package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class UserTransaction(
        @Id
        val id: String?,

        @DBRef
        val card: Card,

        val price: Double,

        val leftBalance: Double,

        val registered: LocalDateTime,

        val type: UserTransactionType,

        val info: UserTransactionInfo?
)

enum class UserTransactionType {
        CARD_BUY
}